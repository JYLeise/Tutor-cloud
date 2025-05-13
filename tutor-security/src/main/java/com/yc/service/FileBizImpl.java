package com.yc.service;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.yc.configs.OSSConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class FileBizImpl implements FileBiz{

    @Autowired
    private OSSConfig ossConfig;

    // 上传文件
    @Async  // 异步调用
    @Override
    public CompletableFuture<String> upload(MultipartFile file) {
        String bucketName = ossConfig.getBucketName();
        String endpoint = ossConfig.getEndpoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        //  新文件名 生成唯一的文件名
        String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = "img/userimg/" +fileName;
        try{
            ossClient.putObject(bucketName, objectName, file.getInputStream());
        }catch (IOException e) {
            log.error("文件流读取失败", e);
            throw new RuntimeException("文件流异常");
        } catch (ClientException e) {
            log.error("客户端异常（网络/AccessKey错误）", e);
        } catch (OSSException e) {
            log.error("服务端异常（权限/Bucket配置问题）", e);
        }finally {
            ossClient.shutdown();
        }
        String path = "https://" + bucketName + "." + endpoint + "/" + objectName;
        log.info("上传文件成功，路径为：{}", path);
        return CompletableFuture.completedFuture(path);
    }

    // 批量上传
    @Override
    public List<String> upload(MultipartFile[] files) {
        List<String> paths = new ArrayList<>();
        for (MultipartFile file : files){
            CompletableFuture<String> future = upload(file);
            try{
                // 等待异步任务完成   get() 方法会阻塞当前线程，直到异步任务完成
                paths.add(future.get());
            }catch (Exception e){
                log.error("上传文件失败", e);
                throw new RuntimeException("上传文件失败");
            }
        }
        return paths;
    }

    @Async
    @Override
    public CompletableFuture<String> generateSignedUrl(String objectName) {
        objectName=objectName.split(".com/")[1];
        OSS ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
        );
        try {
            // 设置 URL 有效期（示例 一天） 1000 * 60 * 60 * 24
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            URL url = ossClient.generatePresignedUrl(
                    ossConfig.getBucketName(),
                    objectName,
                    expiration
            );
            return CompletableFuture.completedFuture(url.toString().replaceAll("%2F","/"));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        } finally {
            ossClient.shutdown();
        }
    }
}

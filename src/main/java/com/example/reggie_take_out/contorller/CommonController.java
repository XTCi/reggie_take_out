package com.example.reggie_take_out.contorller;

import com.example.reggie_take_out.common.R;
import com.sun.deploy.net.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 进行文件的上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
//        原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffiux = originalFilename.substring(originalFilename.lastIndexOf("."));
//        使用uuid随机生成文件名，防止文件名重复早成覆盖
        String filename = UUID.randomUUID().toString() + suffiux;


        log.info(file.toString());
//        创建一个目录对象
        File dir = new File(basePath);
//        如果不存在，创建目录
        if(!dir.exists()){
            dir.mkdirs();
        }

        try {
//            将临时文件存放到指定目录
            file.transferTo(new File(basePath+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(filename);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
//            输入流，通过输入读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
//            输出流，通过输出流将文件显示在浏览器中
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();


            }
            fileInputStream.close();
            outputStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


}

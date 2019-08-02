package top.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.takefly.core.pojo.entity.Result;
import pojo.utils.FastDFSClient;

@RestController
public class UploadFile {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/uploadImage")
    public Result uploadImage(MultipartFile file){
        try{
            //首先获取地址
            FastDFSClient client = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            //name是
            String name = file.getOriginalFilename();
            String extendName = name.substring(name.lastIndexOf("."));
            String addStr = client.uploadFile(file.getBytes(), extendName);
            System.out.println(addStr);
            String url = FILE_SERVER_URL+addStr;

            return new Result(true , url);
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false , null);
        }
    }
}

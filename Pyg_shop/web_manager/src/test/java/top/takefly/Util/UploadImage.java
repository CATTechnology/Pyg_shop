package top.takefly.Util;

import org.junit.Before;
import org.junit.Test;
import pojo.utils.FastDFSClient;

public class UploadImage {

    FastDFSClient fastDFSClient = null;

    private String FILE_SERVER_URL = "http://119.23.64.69/";

    @Before
    public void init() throws Exception{
        fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
    }

    @Test
    public void upload() throws Exception{
        String fileName = fastDFSClient.uploadFile("C:\\Users\\DLF\\Desktop\\desktop.png", ".png");
        System.out.println(FILE_SERVER_URL+fileName);
    }
    
}

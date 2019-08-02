package pojo;


import org.csource.fastdfs.*;
import org.junit.Before;
import org.junit.Test;

public class FastDFSTest {

    private StorageClient storageClient;

    @Before
    public void init() throws Exception{
        //加载fastDFS配置文件
        ClientGlobal.init("D:\\javaEE\\IDEA_Project\\PYG_project\\Pyg_shop\\web_shop\\src\\main\\resources\\fastDFS\\fdfs_client.conf");
        //创建TrackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //通过client获取服务端
        TrackerServer trackerServer = trackerClient.getConnection();
        //声明StorageServer
        StorageServer storageServer = null;
        //创建StorageClient
        storageClient = new StorageClient(trackerServer, null);
    }

    @Test
    public void demo1() throws Exception{
        //测试fastDFS
        String[] infos = storageClient.upload_file("C:\\Users\\DLF\\Desktop\\big_mm.jpg", "jpg", null);
        //遍历回馈信息
        for(String info:infos){
            System.out.println(info);
        }
    }
}

package lab1.rpcClient;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/27/22
 **/

import lab1.api.bean.Transaction;
import lab1.api.bean.basic.Address;
import lab1.api.bean.basic.Product;
import lab1.api.util.SerializeUtils;
import lab1.rpcClient.proxy.RPCInvocationHandler;
import lab1.rpcServer.service.HelloService;
import lab1.rpcClient.proxy.ProxyFactory;
import lab1.rpcServer.service.PurchaseService;
import lab1.rpcServer.service.impl.LookupServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * RPC客户端, 使用Socket与服务端通信
 *
 */
public class RpcClient {

    public static Object send(byte[] bs, Address address) {
        Socket socket = null;
        OutputStream outputStream = null;
        InputStream in = null;

        try {
            socket = new Socket(address.getDomain(), address.getPort());

            outputStream = socket.getOutputStream();

            outputStream.write(bs);

            in = socket.getInputStream();
            byte[] buf = new byte[1024];
            in.read(buf);

            Object formatData = SerializeUtils.deserialize(buf);

            return formatData;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        throw new RuntimeException("Fail to send data!");
    }

    /**
     * 运行main, 开启客户端
     *
     * @param args
     */
    public static void main(String[] args) {
        Address address = new Address("127.0.0.1", 9999);
        RPCInvocationHandler handler = new RPCInvocationHandler(address);
//        System.out.println(HelloService.class);
//        HelloService helloService = ProxyFactory.getInstance(HelloService.class, handler);
//        System.out.println("say: " + helloService.sayHello("zhangsan"));
//        System.out.println("Person: " + helloService.getPerson("zhangsan"));
//        System.out.println("Adress: " + helloService.getPerson("zhangsan").getAddress().toString());
        PurchaseService purchaseService = ProxyFactory.getInstance(PurchaseService.class, handler);
        UUID transactionId = UUID.randomUUID();
        Transaction transaction = new Transaction(transactionId,
                new Address("localhost", 1111),
                transactionId,
                0,
                new Product("0:Fish"),
                1,
                System.currentTimeMillis(),
                5);
        purchaseService.purchase(transaction);
    }

}

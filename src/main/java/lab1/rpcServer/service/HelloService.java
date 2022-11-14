package lab1.rpcServer.service;

import lab1.api.bean.Person;

// 公共服务接口类
public interface HelloService {
    String sayHello(String name);

    Person getPerson(String name);

//    Boolean lookUpReceiver();
}

package lab1.rpcClient.proxy;

import lab1.api.bean.basic.Address;
import lab1.api.bean.basic.NetModel;
import lab1.api.util.SerializeUtils;
import lab1.rpcClient.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/27/22
 **/
public class RPCInvocationHandler implements InvocationHandler {
    private Address rpcAddress;

    public RPCInvocationHandler(Address rpcAddress) {
        this.rpcAddress = rpcAddress;
    }

    public Address getRpcAddress() {
        return rpcAddress;
    }

    public void setRpcAddress(Address rpcAddress) {
        this.rpcAddress = rpcAddress;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        NetModel netModel = new NetModel();

        Class<?>[] classes = proxy.getClass().getInterfaces();
        String className = classes[0].getName();

        netModel.setClassName(className);
        netModel.setArgs(args);
        netModel.setMethod(method.getName());
        String[] types = null;

        if (args != null) {
            types = new String[args.length];
            for (int i = 0; i < types.length; i++) {
                types[i] = args[i].getClass().getName();
            }
        }
        netModel.setTypes(types);

        byte[] bytes = SerializeUtils.serialize(netModel);

        return RpcClient.send(bytes, this.rpcAddress);
    }
}

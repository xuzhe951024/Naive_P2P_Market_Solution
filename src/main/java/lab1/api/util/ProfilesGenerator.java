package lab1.api.util;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Template;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lab1.api.bean.basic.Address;
import lab1.api.bean.basic.Product;
import lab1.api.bean.config.InitConfigForBuyer;
import lab1.api.bean.config.InitConfigForSeller;
import lab1.api.bean.config.ProfilesBean;
import lab1.api.bean.config.basic.InitConfigBasic;
import lab1.api.bean.freemarker.DockerComposeFileModel;

import java.io.*;
import java.util.*;

import static lab1.constants.Const.*;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/28/22
 **/
public class ProfilesGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        YamlReader reader = new YamlReader(new FileReader(args[0]));
        ProfilesBean profilesBean = reader.read(ProfilesBean.class);
        Integer sleepBeforeStart = profilesBean.getSleepBeforeStart();
        Integer maxJump = profilesBean.getMaxJump();
        List<Address> addressesList = createAddressList(profilesBean.getBuyerNumber() + profilesBean.getSellerNumber(),
                profilesBean.getPort(),
                profilesBean.getDeployOnSingleComputer());

        Map<String, List<InitConfigBasic>> sellerBuyerNeighbourInitMap = createNeighbours(addressesList,
                profilesBean.getBuyerNumber(),
                profilesBean.getSellerNumber(),
                profilesBean.getNeighbourNum());

        Map<String, List<InitConfigBasic>> readyMap = addProductAndStock(sellerBuyerNeighbourInitMap,
                profilesBean.getProductNameList(),
                profilesBean.getMaxmumStok());

        generateProfiles(readyMap,
                profilesBean.getDeployOnSingleComputer(),
                sleepBeforeStart, maxJump,
                profilesBean.getMaxmumStok(),
                profilesBean.getNumberOfTests());
    }

    private static Map<String, List<InitConfigBasic>> addProductAndStock(Map<String, List<InitConfigBasic>> sellerBuyerNeighbourInitMap, List<String> productNameList, Integer maxmumStok) {
        Random ra = new Random();

        List<Product> productList = new ArrayList<>();
        Integer productListSize = productNameList.size();
        for (int i = 0; i < productListSize; i++) {
            productList.add(new Product(
                    i, productNameList.get(i)
            ));
        }

        List<InitConfigBasic> sellerList = sellerBuyerNeighbourInitMap.get(ROLE_SELLER);
        List<InitConfigBasic> buyerList = sellerBuyerNeighbourInitMap.get(ROLE_BUYER);
        for (InitConfigBasic ele : sellerList) {
            InitConfigForSeller seller = (InitConfigForSeller) ele;
            seller.setProducts(new ArrayList<>(productList));
            Integer productId = ra.nextInt(productListSize);
            seller.getStock().put(productList.get(productId), ra.nextInt(maxmumStok));
        }

        for (InitConfigBasic ele : buyerList) {
            InitConfigForBuyer buyer = (InitConfigForBuyer) ele;
            buyer.setProducts(new ArrayList<>(productList));
        }
        Map<String, List<InitConfigBasic>> result = new HashMap<>();
        result.put(ROLE_SELLER, sellerList);
        result.put(ROLE_BUYER, buyerList);
        return result;
    }


    private static Map<String, List<InitConfigBasic>> createNeighbours(List<Address> addressesList, Integer buyerNumber, Integer sellerNumber, Integer neighbourNum) {
        Random ra = new Random();
        List<InitConfigBasic> sellerList = new LinkedList<>();
        List<InitConfigBasic> buyerList = new LinkedList<>();
        List<InitConfigBasic> commonList = new LinkedList<>();
        for (int i = 0; i < buyerNumber; i++) {
            InitConfigForBuyer buyer = new InitConfigForBuyer();
            buyer.setSelfAdd(addressesList.get(0));
            addressesList.remove(0);
            buyer.setNeighbours(new ArrayList<Address>());
            commonList.add(buyer);
        }
        for (int i = 0; i < sellerNumber; i++) {
            Integer insertIndex = ra.nextInt(commonList.size());
            InitConfigForSeller seller = new InitConfigForSeller();
            seller.setSelfAdd(addressesList.get(0));
            addressesList.remove(0);
            seller.setNeighbours(new ArrayList<Address>());
            seller.setStock(new HashMap<Product, Integer>());
            commonList.add(insertIndex, seller);
        }

        Integer headTaken = neighbourNum / TWO;
        Integer tailTaken = neighbourNum - headTaken;

        Integer peerNum = buyerNumber + sellerNumber;

        for (int i = 0; i < peerNum; i++) {
            for (int j = 1; j <= headTaken; j++) {
                if (0 > i - j) {
                    commonList.get(i).getNeighbours().add(
                            commonList.get(
                                    peerNum + (i - j)
                            ).getSelfAdd()
                    );
                } else {
                    commonList.get(i).getNeighbours().add(
                            commonList.get(
                                    i - j
                            ).getSelfAdd()
                    );
                }
            }
            for (int j = 1; j <= tailTaken; j++) {
                if (peerNum <= (i + j)) {
                    commonList.get(i).getNeighbours().add(
                            commonList.get(
                                    (i + j) - peerNum
                            ).getSelfAdd()
                    );
                } else {
                    commonList.get(i).getNeighbours().add(
                            commonList.get(
                                    i + j
                            ).getSelfAdd()
                    );
                }
            }
        }

        for (InitConfigBasic ele : commonList) {
            if (ele.getClass().equals(InitConfigForSeller.class)) {
                sellerList.add(ele);
            } else {
                buyerList.add(ele);
            }
        }
        Map<String, List<InitConfigBasic>> result = new HashMap<>();
        result.put(ROLE_SELLER, sellerList);
        result.put(ROLE_BUYER, buyerList);
        return result;
    }

    private static List<Address> createAddressList(int peerNumber, Integer port, Boolean deployOnSingleComputer) {
        List<Address> addressList = new LinkedList<>();
        for (int i = 0; i < peerNumber; i++) {
            Address address = new Address();
            address.setDomain(DOMAIN_PREFIX + i + DOMAIN_SUFIX);
            address.setPort(deployOnSingleComputer ? port + i : port);
            addressList.add(address);
        }
        return addressList;
    }

    private static void generateProfiles(Map<String, List<InitConfigBasic>> sellerBuyerNeighbourInitMap,
                                         Boolean deployOnSingleComputer,
                                         Integer sleepBeforeStart,
                                         Integer maxJump,
                                         Integer maxStock,
                                         Integer numberOfTests) throws IOException, TemplateException {
        List<InitConfigBasic> sellerList = sellerBuyerNeighbourInitMap.get(ROLE_SELLER);
        List<InitConfigBasic> buyerList = sellerBuyerNeighbourInitMap.get(ROLE_BUYER);
        StringBuffer hostSB = new StringBuffer();
        StringBuffer argsSB = new StringBuffer();

        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File(FREE_MARKER_TEMPLATE_DIR));

        Map<String, Object> root = new HashMap<String, Object>();
        List<DockerComposeFileModel> dockerComposeFileModelList = new ArrayList<DockerComposeFileModel>();

        ObjectMapper mapper = new ObjectMapper();
        for (InitConfigBasic ele : sellerList) {
            InitConfigForSeller seller = (InitConfigForSeller) ele;
            seller.setMaxStock(maxStock);
            String jsonDir = seller.getSelfAdd().getDomain() + SLASH + JSON_PROFILE_DIR_BASE;
            File dir = new File(jsonDir);
            dir.mkdirs();
            String jsonFileName = jsonDir + SLASH + JSON_INIT_FILE_NAME;
            File jsonFile = new File(jsonDir + SLASH + JSON_INIT_FILE_NAME);
            mapper.writeValue(jsonFile, seller);

            hostSB.append(LOCALHOST_IP + SPACE + seller.getSelfAdd().getDomain() + ENTER);
            String argString;
            if (deployOnSingleComputer) {
                argString = ROLE_SELLER + SPACE + jsonFileName + SPACE + sleepBeforeStart + SPACE + numberOfTests;
                argsSB.append(argString + ENTER + ENTER);
            } else {
                argString = ROLE_SELLER +
                        SPACE +
                        CURRENT_DIR + JSON_PROFILE_DIR_BASE + SLASH + JSON_INIT_FILE_NAME +
                        SPACE +
                        sleepBeforeStart +
                        SPACE +
                        numberOfTests;
                String runString = RUN_CMD + SPACE + argString;
                String runFileName = seller.getSelfAdd().getDomain() + SLASH + RUN_BASH_FILE;
                BufferedWriter outRun = new BufferedWriter(new FileWriter(runFileName));
                outRun.write(runString);
                outRun.close();

                DockerComposeFileModel model = new DockerComposeFileModel();
                model.setServiceName(seller.getSelfAdd().getDomain());
                model.setWorkingDir(CURRENT_DIR + seller.getSelfAdd().getDomain());
                dockerComposeFileModelList.add(model);
            }
        }

        for (InitConfigBasic ele : buyerList) {
            InitConfigForBuyer buyer = (InitConfigForBuyer) ele;
            buyer.setMaxJump(maxJump);
            String jsonDir = buyer.getSelfAdd().getDomain() + SLASH + JSON_PROFILE_DIR_BASE;
            File dir = new File(jsonDir);
            dir.mkdirs();
            String jsonFileName = jsonDir + SLASH + JSON_INIT_FILE_NAME;
            File jsonFile = new File(jsonFileName);
            mapper.writeValue(jsonFile, buyer);

            hostSB.append(LOCALHOST_IP + SPACE + buyer.getSelfAdd().getDomain() + ENTER);
            String argString;
            if (deployOnSingleComputer) {
                argString = ROLE_BUYER + SPACE + jsonFileName + SPACE + sleepBeforeStart + SPACE + numberOfTests;
                argsSB.append(argString + ENTER + ENTER);
            } else {
                argString = ROLE_BUYER +
                        SPACE +
                        CURRENT_DIR + JSON_PROFILE_DIR_BASE + SLASH + JSON_INIT_FILE_NAME +
                        SPACE +
                        sleepBeforeStart +
                        SPACE +
                        numberOfTests;
                String runString = RUN_CMD + SPACE + argString;
                String runFileName = buyer.getSelfAdd().getDomain() + SLASH + RUN_BASH_FILE;
                BufferedWriter outRun = new BufferedWriter(new FileWriter(runFileName));
                outRun.write(runString);
                outRun.close();

                DockerComposeFileModel model = new DockerComposeFileModel();
                model.setServiceName(buyer.getSelfAdd().getDomain());
                model.setWorkingDir(CURRENT_DIR + buyer.getSelfAdd().getDomain());
                dockerComposeFileModelList.add(model);
            }
        }

        if (deployOnSingleComputer) {
            String hostsFile = HOSTS_FILE;
            BufferedWriter outHost = new BufferedWriter(new FileWriter(hostsFile));
            outHost.write(hostSB.toString());
            outHost.close();

            String argsFile = ARGS_FILE;
            BufferedWriter outArg = new BufferedWriter(new FileWriter(argsFile));
            outArg.write(argsSB.toString());
            outArg.close();
        } else {
            root.put(MODEL_LIST, dockerComposeFileModelList);
            Writer fileWriter = new FileWriter(new File(DOCKER_COMPOSE_FILE));
            try {
                Template template = cfg.getTemplate(FREE_MARKER_TEMPLATE_FILE_NAME);
                template.process(root, fileWriter);
            } finally {
                fileWriter.close();
            }
        }
    }

}

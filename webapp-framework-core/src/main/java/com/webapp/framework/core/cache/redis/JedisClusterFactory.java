package com.webapp.framework.core.cache.redis;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

@Repository(value="jedisClusterFactory")
public class JedisClusterFactory implements FactoryBean<JedisCluster>, InitializingBean{
    
    private Resource resourceAddress;
    private String addressKeyPrefix ;
    private JedisCluster jedisCluster;
    private String timeout ;
    private String maxRedirections ;
    private GenericObjectPoolConfig genericObjectPoolConfig ;
    
    public GenericObjectPoolConfig getGenericObjectPoolConfig(){
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxWaitMillis(1000);
        genericObjectPoolConfig.setMaxTotal(20000);
        genericObjectPoolConfig.setMaxIdle(20);
        genericObjectPoolConfig.setTestOnBorrow(true);
        return genericObjectPoolConfig;
    }
    
    private Pattern p = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

    @Override
    public JedisCluster getObject() throws Exception {
        afterPropertiesSet();
        return jedisCluster;
    }

    @Override
    public Class<? extends JedisCluster> getObjectType() {
        return (this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private Set<HostAndPort> parseHostAndPort() throws Exception {
        try {
            Properties prop = new Properties();
            //读取属性文件  PS：我的项目有问题，Spring 注入不进去 不知道咋回事，所以我就用了这种方式  给redis 做了初始化
//            InputStream in = getClass().getClassLoader().getResourceAsStream("/conf/redis.properties");  
//            prop.load(in);
            
            PropertiesLoaderUtils.fillProperties(prop, this.resourceAddress);
            Set<HostAndPort> haps = new HashSet<HostAndPort>();
            if(addressKeyPrefix == null){
                addressKeyPrefix ="address";
            }
            for (Object key : prop.keySet()) {
                
                if (!((String) key).startsWith(addressKeyPrefix)) {
                    continue;
                }

                String val = (String) prop.get(key);

                boolean isIpPort = p.matcher(val).matches();

                if (!isIpPort) {
                    throw new IllegalArgumentException("ip 或 port 不合法");
                }
                String[] ipAndPort = val.split(":");

                HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                haps.add(hap);
            }

            return haps;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new Exception("解析 jedis 配置文件失败", ex);
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Set<HostAndPort> haps = this.parseHostAndPort();
        if(genericObjectPoolConfig == null){
            genericObjectPoolConfig = getGenericObjectPoolConfig();
        }
        jedisCluster = new JedisCluster(haps,genericObjectPoolConfig);
    }
    
    public void setResourceAddress(Resource resourceAddress) {
        this.resourceAddress = resourceAddress;
    }
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }
    public void setMaxRedirections(String maxRedirections) {
        this.maxRedirections = maxRedirections;
    }
    public void setAddressKeyPrefix(String addressKeyPrefix) {
        this.addressKeyPrefix = addressKeyPrefix;
    }
    public void setGenericObjectPoolConfig(GenericObjectPoolConfig genericObjectPoolConfig) {
        this.genericObjectPoolConfig = genericObjectPoolConfig;
    }

}
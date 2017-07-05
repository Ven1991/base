package com.webapp.framework.core.cache.oscache;

import java.io.Serializable;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.events.CacheEvent;

/**            
 * 序列信息类            
 * @author slzs            
 * Nov 29, 2012 9:37:17 AM            
 * each engineer has a duty to keep the code elegant            
 */  
@SuppressWarnings("serial")             
public class SerialCacheEvent extends CacheEvent implements Serializable {             
                              
    private Cache map = null;             
                              
    private CacheEntry entry = null;             
                              
    public SerialCacheEvent(Cache map, CacheEntry entry) {             
        this(map, entry, null);             
    }             
                              
    public SerialCacheEvent(Cache map, CacheEntry entry, String origin) {             
        super(origin);             
        this.map = map;             
        this.entry = entry;             
    }             
                              
    public CacheEntry getEntry() {             
        return entry;             
    }             
                              
    public String getKey() {             
        return entry.getKey();             
    }             
                              
    public Cache getMap() {             
        return map;             
    }             
                              
    public String toString() {             
        return "key=" + entry.getKey();             
    }             
}  
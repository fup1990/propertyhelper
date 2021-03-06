package com.gome.fup.easyconfig.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gome.fup.easyconfig.common.Cache;
import com.gome.fup.easyconfig.common.Config;
import com.gome.fup.easyconfig.common.Constant;
import com.gome.fup.easyconfig.common.Metadata;
import com.gome.fup.easyconfig.mapper.ConfigMapper;
import com.gome.fup.easyconfig.mapper.MetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by fupeng-ds on 2017/6/13.
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigMapper propertyMapper;
    @Autowired
    private MetadataMapper metadataMapper;

    @Override
    public Config getPropertyById(long id) {
        return propertyMapper.getPropertyById(id);
    }

    @Override
    public List<Config> getPropertyByProjectIdAndGroupName(String projectId, String groupName, String key) {
        return propertyMapper.getPropertyByProjectIdAndGroupName(projectId, groupName, key);
    }

    @Override
    @Transactional
    public void save(Config config, String data) {
        if (null == config.getId()) {
            propertyMapper.insert(config);
        } else {
            propertyMapper.edit(config);
            //将config设置为变更状态，存入缓存
            isChanged(config.getProjectId(), config.getGroupName());
        }
        metadataMapper.deleteByConfigId(config.getId());
        saveMetadata(config.getId(), data);
    }

    private void saveMetadata(Long configId, String data) {
        //String separator = System.getProperty("line.separator", "\n");
        String[] entrySet = data.trim().split("\n");
        for (String entry : entrySet) {
            String[] split = entry.split("=");
            Metadata metadata = new Metadata();
            metadata.setKey(split[0].trim());
            metadata.setValue(split[1].trim());
            metadata.setConfigId(configId);
            metadataMapper.insert(metadata);
        }
    }

    @Override
    public void edit(Config config) {
        propertyMapper.edit(config);
    }

    @Override
    public List<Config> queryConfigByParam(String projectId, String groupName) {
        return propertyMapper.queryConfigByParam(projectId, groupName);
    }

    @Override
    public List<Config> search(String projectId, String groupName) {
        return propertyMapper.search(projectId, groupName);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        metadataMapper.deleteByConfigId(id);
        propertyMapper.deleteById(id);
    }

    private void isChanged(String projectId, String groupName) {
        Cache.put(projectId + Constant.SEPARATE_SYMBOL + groupName, true);
    }

    public PageInfo<Config> page(String projectId, String groupName) {
        PageHelper.startPage(1, 10);
        List<Config> configs = propertyMapper.queryConfigByParam(projectId, groupName);
        return new PageInfo<>(configs);
    }

}

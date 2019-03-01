package com.moxi.remotefilemanager.filedata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * EasyUI JSON转换工具类
 *
 * @author Administrator
 */
public class EasyUIJsonUtil {
    /**
     * 将对象集合转换成树节点集合
     *
     * @param list      数据
     * @param converter 转换器
     * @return 转换后的数据
     */
    public static List<EasyUITreeNode> convert(List<?> list,
                                               EasyUITreeConverter converter) {

        List<EasyUITreeNode> resultList = new ArrayList<EasyUITreeNode>();

        List<EasyUITreeNode> tempList = new ArrayList<EasyUITreeNode>();
        for (Object obj : list) {
            EasyUITreeNode node = converter.convert(obj);
            if (node != null) {
                tempList.add(node);
            }
        }

        for (EasyUITreeNode node : tempList) {
            if (node.getParentId().equals("0")) {
                resultList.add(node);
                addChildren(node, tempList);
            }
        }
        return resultList;
    }

    /**
     * 递归添加子节点的内部方法
     *
     * @param node     节点
     * @param tempList 处理后的数据集合
     */
    private static void addChildren(EasyUITreeNode node,
                                    List<EasyUITreeNode> tempList) {

        for (EasyUITreeNode childNode : tempList) {
            if (!childNode.getParentId().equals("0")
                    && childNode.getParentId().equals(node.getId())) {
                node.getChildren().add(childNode);
                addChildren(childNode, tempList);
            }
        }
    }

    /**
     * 将实体对象集合转换成Map对象集合，用于支持Treegrid组件的数据格式
     *
     * @param list   集合、
     * @param pIdKey pidKey
     * @param idKey  idKey
     * @return 树型集合对象
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> convert(List<?> list,
                                                    String pIdKey, String idKey) {
        List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd";
        for (Object obj : list) {
            String jsonObj = JSON.toJSONString(obj,
                    SerializerFeature.DisableCircularReferenceDetect,
                    SerializerFeature.WriteDateUseDateFormat);
            Map<String, Object> mapObj = JSON.parseObject(jsonObj,
                    TreeMap.class);
            tempList.add(mapObj);
        }

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        List<String> rootIdList = new ArrayList<String>();
        for (Map<String, Object> mapObj : tempList) {
            String id = mapObj.get(idKey).toString();
            Object pId = mapObj.get(pIdKey);
            if (pId == null) {
                resultList.add(mapObj);
                rootIdList.add(id);
            }
        }

        for (String rootId : rootIdList) {
            for (int i = 0; i < tempList.size(); i++) {
                Map<String, Object> mapObj = tempList.get(i);
                String id = mapObj.get(idKey).toString();
                if (rootId.equals(id)) {
                    tempList.remove(i);
                    break;
                }
            }
        }

        for (Map<String, Object> mapObj : resultList) {
            addChildren(mapObj, tempList, pIdKey, idKey);
        }

        return resultList;
    }

    /**
     * 递归添加子节点的内部方法，用于支持Treegrid组件
     *
     * @param mapObj   单条源数据
     * @param tempList 结果数据
     * @param idKey    id字段名称
     * @param pIdKey   pId字段民称
     */
    private static void addChildren(Map<String, Object> mapObj,
                                    List<Map<String, Object>> tempList, String pIdKey,
                                    String idKey) {

        String id = mapObj.get(idKey).toString();

        List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
        List<String> childIdList = new ArrayList<String>();
        for (Map<String, Object> subMapObj : tempList) {
            String childId = subMapObj.get(idKey).toString();
            Object jsonObj = subMapObj.get(pIdKey);
            if (jsonObj != null) {
                String pid = jsonObj.toString();
                if (id.equals(pid)) {
                    childList.add(subMapObj);
                    childIdList.add(childId);
                }
            }
        }

        for (String childId : childIdList) {
            for (int i = 0; i < tempList.size(); i++) {
                Map<String, Object> subMapObj = tempList.get(i);
                String subId = subMapObj.get(idKey).toString();
                if (childId.equals(subId)) {
                    tempList.remove(i);
                    break;
                }
            }
        }

        if (childList.size() > 0) {
            mapObj.put("children", childList);
        }

        for (Map<String, Object> subMapObj : childList) {
            addChildren(subMapObj, tempList, pIdKey, idKey);
        }
    }
}

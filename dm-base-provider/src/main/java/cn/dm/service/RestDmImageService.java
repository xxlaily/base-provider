package cn.dm.service;

import cn.dm.common.Constants;
import cn.dm.common.EmptyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.dm.mapper.DmImageMapper;
import cn.dm.pojo.DmImage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by zezhong.shang on 18-5-15.
 */
@RestController
public class RestDmImageService {
    private static final Logger logger = LoggerFactory.getLogger(RestDmImageService.class);
    @Autowired
    private DmImageMapper dmImageMapper;

    @RequestMapping(value = "/getDmImageById", method = RequestMethod.POST)
    public DmImage getDmImageById(@RequestParam("id") Long id) throws Exception {
        DmImage dmImage = dmImageMapper.getDmImageById(id);
        return setDefaultImg(dmImage);
    }

//    @RequestMapping(value = "/getDmImageListByMap", method = RequestMethod.POST)
//    public List<DmImage> getDmImageListByMap(@RequestBody Map<String, Object> param) throws Exception {
//        List<DmImage> dmImageList = dmImageMapper.getDmImageListByMap(param);
//        return setDefaultImgList(dmImageList);
//    }

    @RequestMapping(value = "/getDmImageCountByMap", method = RequestMethod.POST)
    public Integer getDmImageCountByMap(@RequestBody Map<String, Object> param) throws Exception {
        return dmImageMapper.getDmImageCountByMap(param);
    }

    @RequestMapping(value = "/qdtxAddDmImage", method = RequestMethod.POST)
    public Integer qdtxAddDmImage(@RequestBody DmImage dmImage) throws Exception {
        if (EmptyUtils.isNotEmpty(dmImage) && EmptyUtils.isNotEmpty(dmImage.getImgUrl())) {
            dmImage.setCreatedTime(new Date());
            dmImage.setImgUrl(dmImage.getImgUrl().substring(dmImage.getImgUrl().lastIndexOf("/") + 1, dmImage.getImgUrl().length()));
        }
        return dmImageMapper.insertDmImage(dmImage);
    }

    @RequestMapping(value = "/qdtxModifyDmImage", method = RequestMethod.POST)
    public Integer qdtxModifyDmImage(@RequestBody DmImage dmImage) throws Exception {
        dmImage.setUpdatedTime(new Date());
        return dmImageMapper.updateDmImage(dmImage);
    }

    @RequestMapping(value = "/queryDmImageList", method = RequestMethod.POST)
    public List<DmImage> queryDmImageList(@RequestParam("targetId") Long targetId,
                                          @RequestParam(value = "type", required = false) Integer type,
                                          @RequestParam(value = "category", required = false) Integer category) throws Exception {
        Map<String, Object> imageParam = new HashMap<String, Object>();
        imageParam.put("targetId", targetId);
        imageParam.put("type", type);
        imageParam.put("category", category);
        List<DmImage> dmImageList = dmImageMapper.getDmImageListByMap(imageParam);
        return setDefaultImgList(dmImageList, category, type);
    }

    public DmImage setDefaultImg(DmImage dmImage) throws Exception {
        if (EmptyUtils.isEmpty(dmImage)) {
            dmImage = new DmImage();
        }
        String defaultImg = null;
        if (EmptyUtils.isEmpty(dmImage.getCategory())) {
            logger.info("[setDefaultImg]" + "category为空，则设置为默认图片DEFAULT_NORMAL");
            defaultImg = Constants.DEFAULT_NORMAL;
        } else if (dmImage.getCategory() == Constants.Image.ImageCategory.user) {
            logger.info("[setDefaultImg]" + "category为0，则设置为用户默认图片DEFAULT_USER");
            defaultImg = Constants.DEFAULT_USER;
        } else if (dmImage.getCategory() == Constants.Image.ImageCategory.item) {
            if (dmImage.getType() == Constants.Image.ImageType.normal) {
                logger.info("[setDefaultImg]" + "category为1，type为0");
                defaultImg = Constants.DEFAULT_NORMAL;
            } else if (dmImage.getType() == Constants.Image.ImageType.carousel) {
                logger.info("[setDefaultImg]" + "category为1，type为1");
                defaultImg = Constants.DEFAULT_CAROUSEL;
            } else if (dmImage.getType() == Constants.Image.ImageType.poster) {
                logger.info("[setDefaultImg]" + "category为1，type为2");
                defaultImg = Constants.DEFAULT_POSTER;
            }
        }
        dmImage.setImgUrl(EmptyUtils.isNotEmpty(dmImage.getImgUrl()) ? dmImage.getImgUrl() : defaultImg);
        dmImage.setImgUrl(Constants.FILE_PRE + dmImage.getImgUrl());
        return dmImage;
    }

    public List<DmImage> setDefaultImgList(List<DmImage> dmImages, Integer category, Integer type) throws Exception {
        if (EmptyUtils.isEmpty(dmImages)) {
            logger.info("[setDefaultImgList]" + "没有查询到任何图片");
            dmImages = new ArrayList<DmImage>();
            DmImage dmImage = new DmImage();
            dmImage.setCategory(category);
            dmImage.setType(type);
            dmImages.add(dmImage);
        }
        for (DmImage dmImage : dmImages) {
            setDefaultImg(dmImage);
        }
        return dmImages;
    }

    public static void main(String[] args) {
        String a = "http://192.168.9.151:8888/524314979315224576.png";
        String name = a.substring(a.lastIndexOf("/") + 1, a.length());
        System.out.println(name);
    }
}

package cn.dm.util;

import cn.dm.common.Constants;
import cn.dm.common.RedisUtils;
import cn.dm.mapper.DmImageMapper;
import cn.dm.pojo.DmImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by hai.dong on 2018/6/22.
 */
@Component
public class SynchRedisImageRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(SynchRedisImageRunner.class);
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private DmImageMapper dmImageMapper;

    @Override
    public void run(String... strings) throws Exception {
        logger.info("项目启动了>>>>>>>>>>>>>>>>>>>>");
        //查询出所有的图片信息
        List<DmImage> dmImageList = dmImageMapper.getDmImageListByMap(null);
        //遍历所有的图片，缓存至redis中
        for (DmImage dmImage : dmImageList) {
            //key的格式为targetId_type_category
            String key = dmImage.getTargetId() + "_" + dmImage.getType() + "_" + dmImage.getCategory();
//            //value的值为图片的url地址,如果用户无头像则给其设置一个默认头像
//            String defaultImage = null;
//            if("0".equals(dmImage.getCategory())){//用户默认头像
//                defaultImage = Constants.FILE_PRE+Constants.DEFAULT_USER;
//            }else if("0".equals(dmImage.getType())){//非用户非轮播图非海报
//                defaultImage =Constants.FILE_PRE+Constants.DEFAULT_NORMAL;
//            }else if("1".equals(dmImage.getType())){//轮播图
//                defaultImage =Constants.FILE_PRE+Constants.DEFAULT_CAROUSEL;
//            }else{//海报
//                defaultImage =Constants.FILE_PRE+Constants.DEFAULT_POSTER;
//            }
            String url = dmImage.getImgUrl() == null ? "" : dmImage.getImgUrl();
            redisUtils.set(Constants.IMAGE_TOKEN_PREFIX + key, url);
        }
        logger.info("图片缓存完成>>>>>>>>>>>>>>>>>>>>");
    }
}

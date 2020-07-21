package tk.mybatis;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @program: hello-spring-boot
 * @description: mybatis基础接口
 * @author: Zhouyuhang
 * @create: 2020-04-18 20:56
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}

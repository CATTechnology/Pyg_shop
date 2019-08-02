package top.takefly.core.dao.address;

import org.apache.ibatis.annotations.Param;
import top.takefly.core.pojo.address.Areas;
import top.takefly.core.pojo.address.AreasQuery;

import java.util.List;

public interface AreasDao {
    int countByExample(AreasQuery example);

    int deleteByExample(AreasQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(Areas record);

    int insertSelective(Areas record);

    List<Areas> selectByExample(AreasQuery example);

    Areas selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Areas record, @Param("example") AreasQuery example);

    int updateByExample(@Param("record") Areas record, @Param("example") AreasQuery example);

    int updateByPrimaryKeySelective(Areas record);

    int updateByPrimaryKey(Areas record);
}
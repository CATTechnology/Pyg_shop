package top.takefly.core.dao.address;

import org.apache.ibatis.annotations.Param;
import top.takefly.core.pojo.address.Cities;
import top.takefly.core.pojo.address.CitiesQuery;

import java.util.List;

public interface CitiesDao {
    int countByExample(CitiesQuery example);

    int deleteByExample(CitiesQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(Cities record);

    int insertSelective(Cities record);

    List<Cities> selectByExample(CitiesQuery example);

    Cities selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Cities record, @Param("example") CitiesQuery example);

    int updateByExample(@Param("record") Cities record, @Param("example") CitiesQuery example);

    int updateByPrimaryKeySelective(Cities record);

    int updateByPrimaryKey(Cities record);
}
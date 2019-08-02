package top.takefly.core.dao.address;

import org.apache.ibatis.annotations.Param;
import top.takefly.core.pojo.address.Provinces;
import top.takefly.core.pojo.address.ProvincesQuery;

import java.util.List;

public interface ProvincesDao {
    int countByExample(ProvincesQuery example);

    int deleteByExample(ProvincesQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(Provinces record);

    int insertSelective(Provinces record);

    List<Provinces> selectByExample(ProvincesQuery example);

    Provinces selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Provinces record, @Param("example") ProvincesQuery example);

    int updateByExample(@Param("record") Provinces record, @Param("example") ProvincesQuery example);

    int updateByPrimaryKeySelective(Provinces record);

    int updateByPrimaryKey(Provinces record);
}
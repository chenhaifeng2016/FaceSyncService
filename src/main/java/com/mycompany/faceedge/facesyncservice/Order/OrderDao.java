package com.mycompany.faceedge.facesyncservice.Order;

import org.apache.ibatis.annotations.*;
import org.springframework.dao.DataAccessException;

import java.util.List;

@Mapper
public interface OrderDao {

    @Select("select count(OrderID) from face.order where OrderID=#{orderID} and Status=#{status}")
    int isOrderExist(Order order)throws DataAccessException;

    @Insert("insert into face.order(OrderID, Status, TenantID, UserType, UserID, EnterStationLineCode, EnterStationCode, ExitStationLineCode, ExitStationCode) values(#{orderID}, #{status}, #{tenantID}, #{userType}, #{userID}, #{enterStationLineCode}, #{enterStationCode}, #{exitStationLineCode}, #{exitStationCode})")
    int insert(Order order)throws DataAccessException;

    @Select("select * from face.order")
    //@Select("use face; select * from order") order是关键字，所以只能使用face.order， 可以改名tblOrder
    List<Order> selectAll() throws DataAccessException;

    /*
    * status=0 未进站
    * status=1 已进站
    * status=2 已出站
    * */

    @Select("select * from face.order where TenantID=#{tenantID} and EnterStationLineCode=#{enterStationLineCode} and EnterStationCode=#{enterStationCode} and (Status=0 or Status=1) and UpdateTime > #{updateTime} and date(CreateTime)=curdate()")
    List<Order> syncEnterOrder(@Param("tenantID") String tenantID,
                               @Param("enterStationLineCode") String enterStationLineCode,
                               @Param("enterStationCode") String enterStationCode,

                               @Param("updateTime") String updateTime) throws DataAccessException;

    @Select("select * from face.order where TenantID=#{tenantID} and ExitStationLineCode=#{exitStationLineCode} and ExitStationCode=#{exitStationCode} and (Status=1 or Status=2) and UpdateTime > #{updateTime} and date(CreateTime)=curdate()")
    List<Order> syncExitOrder(@Param("tenantID") String tenantID,
                              @Param("exitStationLineCode") String exitStationLineCode,
                              @Param("exitStationCode") String exitStationCode,

                              @Param("updateTime") String updateTime) throws DataAccessException;


    @Update("update face.order set status=#{status} where orderID=#{orderID}")
    int updateOrderStatus(@Param("orderID") String orderID, @Param("status") int status);
}

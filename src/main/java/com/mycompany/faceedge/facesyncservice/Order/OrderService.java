package com.mycompany.faceedge.facesyncservice.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;


    public boolean isOrderExist(Order order) {
        if (orderDao.isOrderExist(order) == 1)
            return true;

        return false;
    }

    public int insert(Order order)throws DataAccessException {
        return orderDao.insert(order);
    }

    public List<Order> getAll() throws DataAccessException {
        return orderDao.selectAll();
    }

    public List<Order> syncEnterOrder(String tenantID, String enterStationLineCode, String enterStationCode, String updateTime) throws DataAccessException {

        return orderDao.syncEnterOrder(tenantID, enterStationLineCode, enterStationCode, updateTime);
    }

    public List<Order> syncExitOrder(String tenantID, String exitStationLineCode, String exitStationCode, String updateTime) throws DataAccessException {

        return orderDao.syncExitOrder(tenantID, exitStationLineCode, exitStationCode, updateTime);
    }

    public int updateOrderStatus(String orderID, int status) {
        return orderDao.updateOrderStatus(orderID, status);
    }
}

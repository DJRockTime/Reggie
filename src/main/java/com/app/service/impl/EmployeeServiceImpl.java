package com.app.service.impl;

import com.app.entiry.Employee;
import com.app.mapper.EmployeeMapper;
import com.app.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}

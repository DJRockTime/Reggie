package com.app.service.impl;

import com.app.entiry.AddressBook;
import com.app.mapper.AddressBookMapper;
import com.app.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}

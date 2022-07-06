package com.app.contorller;

import com.app.common.BaseContext;
import com.app.common.R;
import com.app.entiry.AddressBook;
import com.app.service.AddressBookService;
import com.app.service.impl.AddressBookServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/address")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     *
     * @param addressBook 地址蒲
     * @return AddressBook
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook: {} ", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook 地址簿
     * @return AddressBook
     */
    @PutMapping
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook: {} ", addressBook);

        /*
         * 先将所有地址的isDefault设置为0
         * */
        // 构造一个更新的 LambdaUpdateWrapper
        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.set(AddressBook::getIsDefault, 0);

        addressBookService.update(queryWrapper);

        /*
         * 再根据id将默认地址设置为1
         * update address_book set is_default = 1 where id = ?
         * */
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 查询默认地址
     * @return AddressBook
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        /*
        * select * from address_book where user_id = ? and is_default = 1;
        * */
        if(null == addressBook) {
            return R.error("没有找到对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     * @param addressBook 地址簿
     * @return List<AddressBook>
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook: {}", addressBook);

        // 条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        /*
        * select * from address_book where user_id = ? order by update_time desc;
        * */
        return R.success(addressBookService.list(queryWrapper));
    }

}

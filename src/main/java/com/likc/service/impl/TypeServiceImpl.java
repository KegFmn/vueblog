package com.likc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.likc.entity.Type;
import com.likc.mapper.TypeMapper;
import com.likc.service.TypeService;
import org.springframework.stereotype.Service;

@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {

}

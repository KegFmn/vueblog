package com.likc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.likc.search.CollectDoc;
import com.likc.vo.SearchBlogVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author likc
 * @date 2022/5/25
 * @description
 */
public interface SearchService extends IService<CollectDoc> {

    Page<SearchBlogVo> search(String keyword, Pageable page);
}

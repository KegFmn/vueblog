package com.likc.controller;

import com.likc.common.lang.Result;
import com.likc.search.CollectDoc;
import com.likc.service.SearchService;
import com.likc.vo.SearchBlogVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author likc
 * @date 2022/5/25
 * @description
 */

@Slf4j
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public Result<Page<SearchBlogVo>> list(@RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage,
                                    @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
                                    @RequestParam(name = "q", defaultValue = "") String q){

        Page<SearchBlogVo> page = searchService.search(q, getPage(currentPage, pageSize));

        return new Result<>(200, "请求成功", page);

    }

    private Pageable getPage(Integer currentPage, Integer pageSize) {
        return PageRequest.of(currentPage - 1, pageSize,
            Sort.by(Sort.Order.desc("updated")));
    }
}

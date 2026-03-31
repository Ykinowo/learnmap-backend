package com.example.learnmapbackend.service;

import com.example.learnmapbackend.entity.Tag;
import com.example.learnmapbackend.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AdminTagService {

    @Autowired
    private TagRepository tagRepository;

    // 分页获取标签列表（按使用次数降序）
    public Page<Tag> getTags(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.isEmpty()) {
            return tagRepository.findByNameContaining(keyword, pageable);
        }
        return tagRepository.findAllByOrderByUseCountDesc(pageable);
    }

    // 添加标签
    @Transactional
    public Tag addTag(String name) {
        Optional<Tag> existing = tagRepository.findByName(name);
        if (existing.isPresent()) {
            throw new RuntimeException("标签已存在");
        }
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepository.save(tag);
    }

    // 编辑标签（修改名称）
    @Transactional
    public Tag updateTag(Long id, String newName) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
        // 检查新名称是否已存在
        Optional<Tag> existing = tagRepository.findByName(newName);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new RuntimeException("标签名已存在");
        }
        tag.setName(newName);
        return tagRepository.save(tag);
    }

    // 删除标签
    @Transactional
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
        // 注意：删除标签时，应将帖子中的该标签移除，但暂时不处理，后续可优化
        tagRepository.delete(tag);
    }

    // 合并标签（将 sourceId 合并到 targetId）
    @Transactional
    public void mergeTags(Long sourceId, Long targetId) {
        Tag source = tagRepository.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("源标签不存在"));
        Tag target = tagRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("目标标签不存在"));
        // 更新所有帖子的 tags 字段，将 source.name 替换为 target.name
        // 此操作较复杂，暂不实现，仅作为演示
        // 合并后删除源标签
        tagRepository.delete(source);
    }
}
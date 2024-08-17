package com.project.mokkozi.controller;

import com.project.mokkozi.model.Mokkozi;
import com.project.mokkozi.service.MokkoziService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mokkozis")
public class MokkoziController {
    @Autowired
    private MokkoziService mokkoziService;

    /**
     * <strong>[Create]</strong> 모꼬지 생성 및 생성된 모꼬지 정보 반환
     * <p>
     * @param mokkozi 생성할 모꼬지 정보
     * @return 생성된 모꼬지 정보
     */
    @PostMapping
    public @ResponseBody ResponseEntity<Mokkozi> createMokkozi(@RequestBody Mokkozi mokkozi) {
        return ResponseEntity.ok(mokkoziService.createMokkozi(mokkozi));
    }

    /**
     * <strong>[Read]</strong> 모든 모꼬지 정보 조회
     * <p>
     * @return 모든 모꼬지 조회 (목록)
     */
    @GetMapping
    public @ResponseBody ResponseEntity readMokkozis() {
        return ResponseEntity.ok(mokkoziService.readMokkozis());
    }

    /**
     * <strong>[Read]</strong> 단일 모꼬지 정보 조회
     * <p>
     * @param id 조회할 모꼬지 id
     * @return id에 해당하는 단일 모꼬지 정보 조회
     */
    @GetMapping
    public @ResponseBody ResponseEntity readMokkozi(@RequestParam(value = "id", required = true) Long id) {
        return ResponseEntity.ok(mokkoziService.readMokkozi(id));
    }

    /**
     * <strong>[Update]</strong> 모꼬지 정보 수정
     * <p>
     * @param id 수정할 모꼬지 id
     * @param mokkozi 수정할 정보가 담긴 mokkozi 객체
     * @return param으로 id가 넘어올 경우 해당 모꼬지 조회, 없을 경우 모든 모꼬지 조회
     */
    @PatchMapping
    public ResponseEntity<Mokkozi> updateMokkozi(@PathVariable @RequestParam(value = "id") Long id, @RequestBody Mokkozi mokkozi) {
        return ResponseEntity.ok(mokkoziService.updateMokkozi(id, mokkozi));
    }

    /**
     * <strong>[Delete]</strong> 모꼬지 삭제
     * <p>
     * @param id 삭제할 모꼬지 id
     * @return 존재하는 모꼬지 id 일 경우 삭제, 그렇지 않을 경우 null 반환
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteMokkozi(@PathVariable @RequestParam(value = "id") Long id) {
        mokkoziService.deleteMokkozi(id);
        return ResponseEntity.ok().build();
    }

}

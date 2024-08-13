package com.project.mokkozi.service;

import com.project.mokkozi.entity.Mokkozi;
import com.project.mokkozi.repository.MokkoziRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MokkoziService {

    @Autowired
    private MokkoziRepository mokkoziRepository;

    public Mokkozi createMokkozi(Mokkozi mokkozi) {
        return mokkoziRepository.save(mokkozi);
    }


    public List<Mokkozi> readMokkozis() {
        return mokkoziRepository.findAll();
    }


    public Mokkozi readMokkozi(Long id) {
        Optional<Mokkozi> readMokkozi = mokkoziRepository.findById(id);
        if(readMokkozi.isPresent()) {
            return readMokkozi.get();
        }
        throw new EntityNotFoundException("Cannot find mokkozi id, id : " + id);
    }

    public Mokkozi updateMokkozi(Long id, Mokkozi mokkozi) {
        Optional<Mokkozi> findMokkozi = mokkoziRepository.findById(id);
        if(!findMokkozi.isPresent()) {
            throw new EntityNotFoundException("Cannot update mokkozi id, id : " + id);
        }

        Mokkozi updateMokkozi = findMokkozi.get();

        updateMokkozi.setTitle(mokkozi.getTitle());
        updateMokkozi.setContents(mokkozi.getContents());
        updateMokkozi.setOwnerId(mokkozi.getOwnerId());
        updateMokkozi.setEndDate(mokkozi.getEndDate());
        updateMokkozi.setMeetDate(mokkozi.getMeetDate());
        updateMokkozi.setMeetPlace(mokkozi.getMeetPlace());
        updateMokkozi.setChatLink(mokkozi.getChatLink());
        updateMokkozi.setRegistDate(mokkozi.getRegistDate());
        updateMokkozi.setLastModifiedDate(mokkozi.getLastModifiedDate());
        updateMokkozi.setClosed(mokkozi.isClosed());

        return mokkoziRepository.save(updateMokkozi);
    }

    /**
     * 모꼬지 제거
     * <p>
     * @param id 삭제할 모꼬지 아이디
     * @return 정상 응답 시 ok 반환
     */
    public String deleteMokkozi(Long id) {
        mokkoziRepository.deleteById(id);
        return "ok";
    }

}
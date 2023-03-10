package com.ghbt.ghbt_starbucks.api.event.service;

import com.ghbt.ghbt_starbucks.api.event.dto.RequestEvent;
import com.ghbt.ghbt_starbucks.api.event.dto.ResponseEvent;
import com.ghbt.ghbt_starbucks.api.event.model.Event;
import com.ghbt.ghbt_starbucks.api.event.repository.IEventRepository;
import com.ghbt.ghbt_starbucks.global.error.ServiceException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class EventServiceImpl implements IEventService {

    private final IEventRepository iEventRepository;

    @Override
    public void addEvent(RequestEvent requestEvent) {
        Event event = Event.builder()
            .description(requestEvent.getDescription())
            .descriptionUrl(requestEvent.getDescriptionUrl())
            .thumbnailUrl(requestEvent.getThumbnailUrl())
            .name(requestEvent.getName()).build();
        iEventRepository.save(event);
    }

    @Override
    public ResponseEvent getEventById(Long id) {
        Event event = iEventRepository.findById(id)
            .orElseThrow(() -> new ServiceException("등록된 이벤트가 없습니다..", HttpStatus.NO_CONTENT));

        return ResponseEvent.builder()
            .description(event.getDescription())
            .descriptionUrl(event.getDescriptionUrl())
            .id(event.getId())
            .name(event.getName())
            .thumbnailUrl(event.getThumbnailUrl()).build();
    }

    @Override
    public ResponseEvent getEventByName(String name) {
        Event event = iEventRepository.findByName(name);

        return ResponseEvent.builder()
            .description(event.getDescription())
            .descriptionUrl(event.getDescriptionUrl())
            .id(event.getId()).name(event.getName())
            .thumbnailUrl(event.getThumbnailUrl()).build();
    }
}

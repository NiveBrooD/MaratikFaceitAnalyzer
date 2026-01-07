package com.ramis.dbsaver.model.mapper;

import com.ramis.dbsaver.model.StatsResponse;
import com.ramis.dbsaver.model.Stats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatMapper {

    @Mapping(target = "id", ignore = true)
    Stats to(StatsResponse statsResponse);
}

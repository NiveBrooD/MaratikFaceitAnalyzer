package com.ramis.dbsaver.model.mapper;

import com.ramis.dbsaver.model.StatsResponse;
import com.ramis.dbsaver.model.Stats;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatMapper {

    Stats to(StatsResponse statsResponse);
}

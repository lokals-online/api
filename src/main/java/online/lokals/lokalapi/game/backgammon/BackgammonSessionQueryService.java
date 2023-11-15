package online.lokals.lokalapi.game.backgammon;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.game.backgammon.api.BetweenQueryResponse;

@Service
@RequiredArgsConstructor
public class BackgammonSessionQueryService {

    // private final MongoTemplate mongoTemplate;


    // public List<BackgammonSession> betweenStatistics(@Nonnull String homeId, @Nonnull String opponentId) {

        // var homeCriteria = new Criteria("home._id").is(homeId).and("away._id").is(opponentId);
        // var awayCriteria = new Criteria("away._id").is(homeId).and("home._id").is(opponentId);
        // var criteria = homeCriteria.orOperator(awayCriteria);

        // var betweenQuery = new Query();
        // betweenQuery.addCriteria(Criteria.where("home._id").is(homeId).and("away._id").is(opponentId));
        // betweenQuery.addCriteria(Criteria.where("away._id").is(homeId).and("home._id").is(opponentId));

        // MatchOperation matchStage = Aggregation.match(new Criteria("home._id").is(homeId));

        // Aggregation aggregation = Aggregation.newAggregation(matchStage);

        // AggregationResults<BetweenQueryResponse> output = mongoTemplate.aggregate(aggregation, "backgammon_session", BetweenQueryResponse.class);
        // return output.getMappedResults();

    //     retu

    // }

}

package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storages.dao.ReviewStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Review> REVIEW_ROW_MAPPER = (rs, rowNum) -> Review.builder()
            .reviewId(rs.getInt("review_id"))
            .content(rs.getString("content"))
            .isPositive(rs.getBoolean("is_positive"))
            .filmId(rs.getInt("film_id"))
            .userId(rs.getInt("user_id"))
            .useful(rs.getInt("useful"))
            .build();

    @Override
    public Review save(Review review) {
        final String sql = String.format("INSERT INTO reviews (content,is_positive,user_id,film_id)" +
                        " VALUES ('%s','%b','%d',%d);",
                review.getContent(), review.getIsPositive(),
                review.getUserId(), review.getFilmId());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> connection.prepareStatement(sql, new String[]{"review_id"}), keyHolder);
        int id = keyHolder.getKey().intValue();
        return find(id);
    }

    @Override
    public boolean exists(int id) {
        String query = "SELECT EXISTS(SELECT * FROM reviews WHERE review_id = ?)";
        return jdbcTemplate.queryForObject(query, new Object[]{id}, Boolean.class);
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?,is_positive = ? " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return find(review.getReviewId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?;";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Review find(int id) {
        String sql = "SELECT r.*, SUM(rl.is_like) AS useful " +
                "FROM reviews AS r " +
                "LEFT JOIN review_likes AS rl ON r.review_id = rl.review_id " +
                "WHERE r.review_id = ? " +
                "GROUP BY r.review_id;";
        return jdbcTemplate.queryForObject(sql, REVIEW_ROW_MAPPER, id);
    }

    @Override
    public List<Review> findAll(int count) {
        String sql = "SELECT r.*, IFNULL(SUM(rl.is_like),0) AS useful " +
                "FROM reviews AS r " +
                "LEFT JOIN review_likes AS rl ON r.review_id = rl.review_id " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, REVIEW_ROW_MAPPER, count);
    }

    @Override
    public List<Review> findAllByFilmId(int filmId, int count) {
        String sql = "SELECT r.*, IFNULL(SUM(rl.is_like),0) AS useful " +
                "FROM reviews AS r " +
                "LEFT JOIN review_likes AS rl ON r.review_id = rl.review_id " +
                "WHERE r.film_id = ? " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, REVIEW_ROW_MAPPER, filmId, count);
    }

    @Override
    public void addLike(int id, int userId) {
        String sql = "MERGE INTO review_likes KEY(review_id, user_id) VALUES(?,?,1);";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void addDislike(int id, int userId) {
        String sql = "MERGE INTO review_likes KEY(review_id, user_id) VALUES(?,?,-1);";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public int deleteDislike(int id, int userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = -1;";
        return jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public int deleteLike(int id, int userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = 1;";
        return jdbcTemplate.update(sql, id, userId);
    }
}

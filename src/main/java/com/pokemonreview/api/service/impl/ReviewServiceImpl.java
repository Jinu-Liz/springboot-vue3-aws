package com.pokemonreview.api.service.impl;

import com.pokemonreview.api.dto.ReviewDto;
import com.pokemonreview.api.exception.ResourceNotFoundException;
import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.models.Review;
import com.pokemonreview.api.repository.PokemonRepository;
import com.pokemonreview.api.repository.ReviewRepository;
import com.pokemonreview.api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final PokemonRepository pokemonRepository;

    private Pokemon getExistPokemon(int pokemonId) {
        Pokemon pokemon = pokemonRepository
                .findById(pokemonId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pokemon with associated review not found"));
        return pokemon;
    }

    private Review getExistReview(int reviewId) {
        Review review = reviewRepository
                .findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review with associate pokemon not found"));
        return review;
    }

    @Override
    public ReviewDto createReview(int pokemonId, ReviewDto reviewDto) {
        Review review = mapToEntity(reviewDto);

        Pokemon pokemon = getExistPokemon(pokemonId);
        review.setPokemon(pokemon);

        Review newReview = reviewRepository.save(review);
        return mapToDto(newReview);
    }


    @Override
    public List<ReviewDto> getReviewsByPokemonId(int pokemonId) {
        List<Review> reviews = reviewRepository.findByPokemonId(pokemonId);

        return reviews.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDto getReviewById(int reviewId, int pokemonId) {
        Pokemon pokemon = getExistPokemon(pokemonId);

        Review review = getExistReview(reviewId);

        if(review.getPokemon().getId() != pokemon.getId()) {
            throw new ResourceNotFoundException(
                    "This review does not belond to a pokemon");
        }

        return mapToDto(review);
    }

    @Override
    public ReviewDto updateReview(int pokemonId, 
                                  int reviewId, 
                                  ReviewDto reviewDto) {
        Pokemon pokemon = getExistPokemon(pokemonId);

        Review review = getExistReview(reviewId);

        if(review.getPokemon().getId() != pokemon.getId()) {
            throw new ResourceNotFoundException(
                    "This review does not belong to a pokemon");
        }

        review.setTitle(reviewDto.getTitle());
        review.setContent(reviewDto.getContent());
        review.setStars(reviewDto.getStars());

        Review updateReview = reviewRepository.save(review);

        return mapToDto(updateReview);
    }

    @Override
    public void deleteReview(int pokemonId, int reviewId) {
        Pokemon pokemon = getExistPokemon(pokemonId);

        Review review = getExistReview(reviewId);

        if(review.getPokemon().getId() != pokemon.getId()) {
            throw new ResourceNotFoundException(
                    "This review does not belong to a pokemon");
        }

        reviewRepository.delete(review);
    }

    private ReviewDto mapToDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        BeanUtils.copyProperties(review, reviewDto);
//        reviewDto.setId(review.getId());
//        reviewDto.setTitle(review.getTitle());
//        reviewDto.setContent(review.getContent());
//        reviewDto.setStars(review.getStars());
        return reviewDto;
    }

    private Review mapToEntity(ReviewDto reviewDto) {
        Review review = new Review();
        review.setId(reviewDto.getId());
        review.setTitle(reviewDto.getTitle());
        review.setContent(reviewDto.getContent());
        review.setStars(reviewDto.getStars());
        return review;
    }
}
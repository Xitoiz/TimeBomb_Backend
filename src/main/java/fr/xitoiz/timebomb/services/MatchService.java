package fr.xitoiz.timebomb.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.xitoiz.timebomb.dao.IDAOCard;
import fr.xitoiz.timebomb.dao.IDAOMatch;
import fr.xitoiz.timebomb.enums.CardState;
import fr.xitoiz.timebomb.enums.CardType;
import fr.xitoiz.timebomb.exeption.TransactionErrorException;
import fr.xitoiz.timebomb.models.Card;
import fr.xitoiz.timebomb.models.Match;
import fr.xitoiz.timebomb.models.User;

@Service
public class MatchService {
	
	@Autowired
	private IDAOMatch daoMatch;
	
	@Autowired
	private IDAOCard daoCard;

	public void generateCard(Match match) {
		int nbUser = match.getPlayerList().size();
		
		int nbDefuse = nbUser;
		int nbBait = 5 * nbUser - 1;
		
		ArrayList<CardType> listOfCardType = new ArrayList<>();
		ArrayList<Card> listOfCards = new ArrayList<>();
		
		listOfCardType.add(CardType.BOMB);
		
		for (int i = 0; i < nbDefuse; i++) {listOfCardType.add(CardType.DIFFUSE);}
		for (int i = 0; i < nbBait; i++) {listOfCardType.add(CardType.BAIT);}
		
		for (CardType cardRole : listOfCardType) {
			Card card = new Card();
			card.setType(cardRole);
			card.setMatch(match);
			listOfCards.add(card);
		}
		
		try {
			this.daoCard.saveAll(listOfCards);
		} catch (Exception e) {
			throw new TransactionErrorException();
		}
		
		match.setCardList(listOfCards);
		
		try {
			this.daoMatch.save(match);
		} catch (Exception e) {
			throw new TransactionErrorException();
		}
		
		distributeCards(match);
	}
	
	public void distributeCards(Match match) {

		List<User> listPlayer = match.getPlayerList();
		
		List<Card> listCard = match.getCardList();
		
		ArrayList<CardType> listCardType = new ArrayList<>();
		ArrayList<User> listUser = new ArrayList<>();
		
		int nbUser = listPlayer.size();
		int nbCardToDistribute = 0;
		
		for (Card card : listCard) {
			if (card.getState().equals(CardState.HIDDEN)) {nbCardToDistribute = nbCardToDistribute + 1;}
		}
		
		nbCardToDistribute = nbCardToDistribute / nbUser;
		
		for (User player : listPlayer) {
			for (int i = 0; i < nbCardToDistribute; i++) {
				listUser.add(player);
			}
		}
		
		for (Card card : listCard) {
			listCardType.add(card.getType());
		}
		
		Collections.shuffle(listCardType);
		
		for (int i = 0; i < listCardType.size(); i++) {
			Card card = listCard.get(i);
			card.setOwner(null);
			if (card.getState().equals(CardState.HIDDEN)) {
				card.setType(listCardType.get(i));
				card.setOwner(listUser.get(i));
				listCard.set(i, card);
			}
		}
		
		this.daoMatch.save(match);
	}
}

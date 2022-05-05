package fr.xitoiz.timebomb.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.xitoiz.timebomb.enums.CardState;
import fr.xitoiz.timebomb.enums.CardType;
import fr.xitoiz.timebomb.match.Match;
import fr.xitoiz.timebomb.user.User;

@Service
public class CardService {
	
	@Autowired
	private CardDAO daoCard;
	
	public void generateCard(Match match) {
		int nbUser = match.getPlayerList().size();
		
		int nbDefuse = nbUser;
		int nbBait = 5 * nbUser - 1;
		
		ArrayList<CardType> listOfCardType = new ArrayList<>();
		List<Card> listOfCards = new ArrayList<>();
		
		listOfCardType.add(CardType.BOMB);
		
		for (int i = 0; i < nbDefuse; i++) {listOfCardType.add(CardType.DIFFUSE);}
		for (int i = 0; i < nbBait; i++) {listOfCardType.add(CardType.BAIT);}
		
		for (CardType cardRole : listOfCardType) {
			Card card = new Card();
			card.setType(cardRole);
			card.setMatch(match);
			listOfCards.add(card);
		}
		
		match.setCardList(listOfCards);

		this.daoCard.saveAll(listOfCards);
	}
	
	public void distributeCards(Match match) {
		List<User> listPlayer = match.getPlayerList();
		List<Card> listCard = match.getCardList();
		
		ArrayList<CardType> listCardType = new ArrayList<>();
		ArrayList<User> listUser = new ArrayList<>();
		
		int nbUser = listPlayer.size();
		int nbCardToDistribute = 0;
		
		for (Card card : listCard) {
			card.setOwner(null);
			if (card.getState().equals(CardState.HIDDEN)) {
				nbCardToDistribute = nbCardToDistribute + 1;
				listCardType.add(card.getType());}
		}
		
		int nbCardForEachHand = nbCardToDistribute / nbUser;
		
		for (User player : listPlayer) {
			for (int i = 0; i < nbCardForEachHand; i++) {
				listUser.add(player);
			}
		}
		
		Collections.shuffle(listCardType);
		
		int indexList = 0;
		
		for (Card card: listCard) {
			if (card.getState().equals(CardState.HIDDEN)) {
				card.setType(listCardType.get(indexList));
				card.setOwner(listUser.get(indexList));
				indexList = indexList + 1;
			}
		}
		
	}

	public void revealAllCards(Match match) {
		List<Card> listOfCard = match.getCardList();
		for (Card card: listOfCard) {
			card.setState(CardState.REVEALED);
		}
	}

}

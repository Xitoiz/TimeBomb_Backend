package fr.xitoiz.timebomb.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import fr.xitoiz.timebomb.enums.CardState;
import fr.xitoiz.timebomb.enums.CardType;
import fr.xitoiz.timebomb.enums.PlayerRole;
import fr.xitoiz.timebomb.exeption.TransactionErrorException;
import fr.xitoiz.timebomb.models.Card;
import fr.xitoiz.timebomb.models.Match;
import fr.xitoiz.timebomb.models.User;

@Service
public class MatchService {

	public Match generateRole(Match match) {
		
		List<User> listOfPlayer = match.getPlayerList();
		ArrayList<PlayerRole> listOfPlayerRole = new ArrayList<>();
		
		switch(listOfPlayer.size()) {
			case 8:
			case 7:
				listOfPlayerRole.add(PlayerRole.MORIARTY);
				listOfPlayerRole.add(PlayerRole.SHERLOCK);
			
			case 6:	
				listOfPlayerRole.add(PlayerRole.SHERLOCK);
			case 5:
			case 4:
				listOfPlayerRole.add(PlayerRole.MORIARTY);
				listOfPlayerRole.add(PlayerRole.MORIARTY);
				listOfPlayerRole.add(PlayerRole.SHERLOCK);
				listOfPlayerRole.add(PlayerRole.SHERLOCK);
				listOfPlayerRole.add(PlayerRole.SHERLOCK);
				break;
			default:
				throw new TransactionErrorException();
			}
		
		Collections.shuffle(listOfPlayerRole);
		
		for (int i = 0; i < listOfPlayer.size(); i++) {
			listOfPlayer.get(i).setRole(listOfPlayerRole.get(i));
		}
		
		return match;
	}
	
	public Match generateCard(Match match) {
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
		
		return match;
	}
	
	public Match distributeCards(Match match) {
		
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
		
		return match;
	}

	public boolean isDefuseLeft(Match match) {
		List<Card> listOfCard = match.getCardList();
		
		for (Card card: listOfCard) {
			if (card.getType() == CardType.DIFFUSE && card.getState() == CardState.HIDDEN) {
				return true;
			}
		}
		return false;
	}

	public boolean isRoundOver(Match match) {
		List<Card> listOfCard = match.getCardList();
		int nbCardRevealedThisTurn = 0;
		int nbPlayer = match.getPlayerList().size();
		
		for (Card card: listOfCard) {
			if (card.getOwner() != null && card.getState() == CardState.REVEALED) {
				nbCardRevealedThisTurn = nbCardRevealedThisTurn + 1;
			}
		}
		
		return (nbCardRevealedThisTurn == nbPlayer)? true : false;
	}

	public boolean isMatchOver(Match match) {
		List<Card> listOfCard = match.getCardList();
		int nbCardHidden = 0;
		int nbPlayer = match.getPlayerList().size();
		
		for (Card card: listOfCard) {
			if (card.getState() == CardState.HIDDEN) {
				nbCardHidden = nbCardHidden + 1;
			}
		}
		
		return (nbCardHidden == nbPlayer)? true : false;
	}

	public Match revealAllCards(Match match) {
		List<Card> listOfCard = match.getCardList();
		for (Card card: listOfCard) {
			card.setState(CardState.REVEALED);
		}
		return match;
	}
}

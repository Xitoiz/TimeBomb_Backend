package fr.xitoiz.timebomb.emitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.xitoiz.timebomb.match.Match;
import fr.xitoiz.timebomb.projection.MatchProjection;
import fr.xitoiz.timebomb.user.User;
import fr.xitoiz.timebomb.user.UserDAO;

@Service
public class MatchEmitterService {
	
	@Autowired
	private UserDAO daoUser;
	
	private final Logger logger = LoggerFactory.getLogger(MatchEmitterService.class);
	private List<MatchEmitter> emitters = new ArrayList<>();
	private List<MatchEmitter> emittersOnError = new ArrayList<>();

	public void send(Match match) {
		ExecutorService sseExecutor = Executors.newSingleThreadExecutor();
		
		sseExecutor.execute(() -> {
			this.logger.debug("Envoie du match {} en Sse en cours ...", match.getId());
			
			for (MatchEmitter emitter : new ArrayList<>(this.emitters)) {
				if (emitter.getMatchId() != null && emitter.getMatchId() == match.getId()) {
					User player = this.daoUser.getById(emitter.getUserId());
					MatchProjection matchProjection = new MatchProjection(match, player);
					this.send(emitter, matchProjection, null);
				}
			}

			this.logger.debug("Envoie du match {} en Sse terminé !", match.getId());
		});
	}

	public void send(Object object, Class<?> view) {
		ExecutorService sseExecutor = Executors.newSingleThreadExecutor();
		
		sseExecutor.execute(() -> {
			this.logger.debug("Envoie Sse en cours ...");
			
			for (MatchEmitter emitter : new ArrayList<>(this.emitters)) {
				if (emitter.getMatchId() == null) {
					this.send(emitter, object, view);
				}
			}

			this.logger.debug("Envoie Sse terminé !");
		});
	}

	public void send(MatchEmitter emitter, Object object, Class<?> view) {
		if (emitter.getSse() == null) {
			this.logger.debug("SseEmitter null, suppression demandée");
			this.emitterCompletion(emitter);
			return;
		}
		
		try {
			this.logger.debug("Envoie de l'objet {} à l'utilisateur {} !", object.getClass().getName(), emitter.getUserId());
			emitter.getSse().send(this.write(object, view));
		}
		
		catch (Exception ex) {
			this.logger.error("Emitter : erreur pour l'utilisateur {} ({})", emitter.getUserId(), ex.getClass());
			emitter.getSse().completeWithError(ex);
		}
	}


	public SseEmitter create(Integer userId) {
		return this.create(userId, null);
	}


	public SseEmitter create(Integer userId, Integer matchId) {
		MatchEmitter emitter = new MatchEmitter(userId, matchId);
		this.emitters.add(emitter);

		emitter.getSse().onCompletion(() -> this.emitterCompletion(emitter));
		emitter.getSse().onTimeout(() -> this.emitterCompletion(emitter));
		emitter.getSse().onError((error) -> this.emitterCompletion(emitter));

		return emitter.getSse();
	}
	
	private void emitterCompletion(MatchEmitter emitter) {
		ExecutorService sseCleaningExecutor = Executors.newSingleThreadExecutor();
		
		sseCleaningExecutor.execute(() -> {
			synchronized (this.emitters) {
				try {
					this.logger.debug("Fin d'un SseEmitter, tentative de suppression");
					this.emitters.remove(emitter);
					
					this.logger.debug("Fin d'un SseEmitter, nettoyage des autres Sse en erreur ...");
					this.emitters.removeAll(this.emittersOnError);
					this.emittersOnError.clear();
					this.logger.debug("Fin d'un SseEmitter, nettoyage OK !");
				}
				
				catch (Exception e) {
					this.logger.debug("Fin d'un SseEmitter, suppression impossible, mise en liste d'attente pour suppression ultérieure");
					emitter.setSse(null);
					this.emittersOnError.add(emitter);
				}
			}
		});
	}

	private String write(Object object, Class<?> view) {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
			return mapper
				.writerWithView(view)
				.writeValueAsString(object);
		}

		catch (Exception e) {
			return null;
		}
	}
}
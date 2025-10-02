package com.docker.service;

import com.docker.entity.Message;
import com.docker.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ce service gère la logique métier liée aux messages. Il utilise le
 * MessageRepository pour interagir avec la base de données.
 */
@Service
public class MessageService {

	private final MessageRepository messageRepository;

	@Autowired
	public MessageService(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	/**
	 * Sauvegarde un nouveau message en base de données. Le timestamp est
	 * automatiquement ajouté avant la sauvegarde.
	 *
	 * @param message L'objet Message à sauvegarder.
	 * @return L'objet Message sauvegardé.
	 */
	public Message saveMessage(Message message) {
		message.setTimestamp(LocalDateTime.now());
		return messageRepository.save(message);
	}

	/**
	 * Récupère la liste de tous les messages.
	 *
	 * @return Une liste d'objets Message.
	 */
	public List<Message> findAllMessages() {
		return messageRepository.findAll();
	}

	/**
	 * Supprime un message par son ID.
	 *
	 * @param id L'ID du message à supprimer.
	 */
	public void deleteMessage(Long id) {
		messageRepository.deleteById(id);
	}

	public long countMessages() {
		return messageRepository.count();
	}
}

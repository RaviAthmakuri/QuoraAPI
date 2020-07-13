package com.upgrad.quora.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "answer")
@NamedQueries({
  @NamedQuery(name = "getAnswerById", query = "select q from AnswerEntity q where q.uuid = :uuid"),
  @NamedQuery(
      name = "getAllAnswersToQuestion",
      query = "select a from AnswerEntity a where a.question.uuid = :uuid")
})
public class AnswerEntity implements Serializable {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "uuid")
  @Size(max = 64)
  private String uuid;

  @Column(name = "ans")
  @NotNull
  private String answer;

  @Column(name = "date")
  @NotNull
  private ZonedDateTime date;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private UserEntity userEntity;

  @ManyToOne
  @JoinColumn(name = "question_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private QuestionEntity question;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public UserEntity getUser() {
    return userEntity;
  }

  public void setUser(UserEntity user) {
    this.userEntity = user;
  }

  public QuestionEntity getQuestion() {
    return question;
  }

  public void setQuestion(QuestionEntity question) {
    this.question = question;
  }
}

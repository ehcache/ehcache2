/*
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *      http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Platform.
 *
 * The Initial Developer of the Covered Software is
 *      Terracotta, Inc., a Software AG company
 */
package org.terracotta.modules.ehcache.hibernate.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Event {
  private Long   id;

  private String title;
  private Date   date;
  private Set    participants = new HashSet();
  private Person organizer;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setOrganizer(Person organizer) {
    this.organizer = organizer;
  }

  public Person getOrganizer() {
    return organizer;
  }

  public Set getParticipants() {
    return participants;
  }

  public void setParticipants(Set participants) {
    this.participants = participants;
  }

  public void addParticipant(Person person) {
    participants.add(person);
    person.getEvents().add(this);
  }

  public void removeParticipant(Person person) {
    participants.remove(person);
    person.getEvents().remove(this);
  }

  public String toString() {
    return getTitle() + ": " + getDate();
  }
}

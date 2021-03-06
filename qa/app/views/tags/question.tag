<div class="question">
	<div class="voting">
		<div class="vote-curr">${question.voting()}
		</div>
			#{secure.check 'user'} #{if user.isAbleToVote(question.id) &&
			user.hasTimeToChange(question.id)}
		<div class="vote-up-down"><a class="vote-up"
				href="@{Users.voteForQuestion(question.id, true)}"></a> <a
				class="vote-down" href="@{Users.voteForQuestion(question.id, false)}"></a>
		</div>	
			#{/if} #{/secure.check}
	</div>
	<div id="question-author" class="author">
		<div class="buttons" id="question-buttons">#{secure.check 'user'}
			#{ifnot question.hasBestAnswer} #{if !user.isFollowing(question)} 
			<a class="button" href="@{Users.followQuestion(question.id)}"> <span>&{'follow'}</span></a> 
			#{/if} #{else} 
			<a class="button" href="@{Users.unfollowQuestion(question.id)}"> <span>&{'unfollow'}</span></a> 
			#{/else} #{if question.isOwnPost()} 
			<a class="button" href="@{Users.showEdit(question.id, question.historys.size()-1)}">&{'edit'}</a>
			#{/if} #{/ifnot} #{/secure.check}</div>
				<div class="question-date">${question.getDate()}
				</div>
				<div id="userImg"><img src="${question.author.avatarPath}" />
				</div>
				<a class="button" href="@{Users.showProfile(question.author.id)}">${question.author.username}(${question.author.rating})</a>
		</div>
		<div class="question-main">
			<div class="question-title">
				<a class="button"href="@{Application.show(question.id)}">${question.getTitle()}</a>
			</div>
			<div class="content" id="question-content">${question.content.raw()}
			</div>
			<div class="question-tag">
				#{if !question.tags.isEmpty()} 
					#{list items:question.tags, as:'tag'} 
						<a class="button" href="@{Application.tagged(tag)}">${tag.getName()}</a>
					#{/list} 
				#{/if}
			</div>
			<div class="attachment" id="question-attachment">
				#{if question.attachmentPath != null} 
					<a class="button" href="${question.attachmentPath}"> <img src="@{'/public/images/file.gif'}" /> Attachment </a> 
				#{/if}
			</div>
			<div class="question-metadata">(&{'nrAnswers'}:${question.answers.size()} | &{'nrComments'}:${question.comments.size()})
			</div>
		</div>
	</div>
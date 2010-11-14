		#{if _activity.isQuestion()}
			A new question was asked <a class="button" href = "@{Application.show(_activity.id)}">${_activity.getTitle()}</a> (${_activity.getDate()}).
		#{/if}
		#{if _activity.isAnswer()}
			A new answer was added to the <a class="button" href = "@{Application.show(_activity.findQuestion().id)}">question</a> ${_activity.findQuestion().title} (${_activity.getDate()}).
		#{/if}
		#{if _activity.isCommentAnswer()}
			A new comment was added to a answer from the <a class="button" href = "@{Application.show(_activity.post.findQuestion().id)}">question</a> ${_activity.post.findQuestion().title}  (${_activity.getDate()}).
		#{/if}
		#{if _activity.isCommentQuestion()}
			A new comment was added to the <a class="button" href = "@{Application.show(_activity.post.id)}">question</a> ${_activity.post.title} (${_activity.getDate()}).
		#{/if}
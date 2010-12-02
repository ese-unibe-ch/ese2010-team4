		#{if _activity.isQuestion()}
			&{'activity.newQuestion'} <a class="button" href = "@{Application.show(_activity.id)}">${_activity.getTitle()}</a> (${_activity.getDate()}).
		#{/if}
		#{if _activity.isAnswer()}
			&{'activity.newAnswer'} <a class="button" href = "@{Application.show(_activity.findQuestion().id)}">${_activity.findQuestion().title}</a> (${_activity.getDate()}).
		#{/if}
		#{if _activity.isCommentAnswer()}
			&{'activity.newComment'} <a class="button" href = "@{Application.show(_activity.post.findQuestion().id)}">${_activity.post.findQuestion().title}</a> (${_activity.getDate()}).
		#{/if}
		#{if _activity.isCommentQuestion()}
			&{'activity.newComment'} <a class="button" href = "@{Application.show(_activity.post.id)}">${_activity.post.title}</a> (${_activity.getDate()}).
		#{/if}
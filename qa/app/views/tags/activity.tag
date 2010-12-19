		#{if _activity instanceof models.Question}
			&{'activity.newQuestion'} <a class="button" href = "@{Application.show(_activity.id)}">${_activity.getTitle()}</a> (${_activity.getDate()}).
		#{/if}
		#{if _activity instanceof models.Answer}
			&{'activity.newAnswer'} <a class="button" href = "@{Application.show(_activity.giveQuestion().id)}">${_activity.giveQuestion().title}</a> (${_activity.getDate()}).
		#{/if}
		#{if _activity instanceof models.Comment && _activity instanceof models.Answer}
			&{'activity.newComment'} <a class="button" href = "@{Application.show(_activity.post.giveQuestion().id)}">${_activity.post.giveQuestion().title}</a> (${_activity.getDate()}).
		#{/if}
		#{if _activity instanceof models.Comment && _activity instanceof models.Question}
			&{'activity.newComment'} <a class="button" href = "@{Application.show(_activity.post.id)}">${_activity.post.title}</a> (${_activity.getDate()}).
		#{/if}
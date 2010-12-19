package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;


/**
 * The Class Post.
 */
@Entity
public abstract class VotablePost extends Post {

	public String fullname;
	public int voting;
	public String attachmentPath;
	
	@OneToMany(mappedBy = "post", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Comment> comments;
	@ManyToMany(cascade = CascadeType.PERSIST)
	public Set<Tag> tags;
	
	@OneToMany(mappedBy = "post", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })	
	public List<Vote> votes;

	/**
	 * Add an vote
	 * 
	 * @param data.user
	 * @param result
	 * @return the voted post
	 */

	public VotablePost(User author, String content) {
		
		super(author, content);
		this.votes = new ArrayList<Vote>();
		this.comments = new ArrayList<Comment>();
		this.tags = new TreeSet<Tag>();
		this.voting = 0;		
	}

	public boolean hasVoted(User comuser) {

		for (Vote vote : votes) {
			if (vote.user.equals(comuser)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Count the positive and negative votes
	 * 
	 * @return votestatus
	 */
	public int voting() {

		int status = 0;
		for (Vote vote : this.votes) {

			if (vote.result) {
				status++;
			} else {
				status--;
			}
		}
		voting = status;
		return status;

	}
	
	
	/**
	 * Adds comments to posts
	 * 
	 * @param the coment which has to be added
	 * @return the post with the added comment.
	 */
	public VotablePost addComment(Comment comment) {
		this.comments.add(comment);
		this.save();
		return this;

	}
	
	/**
	 * Adds tags to the posts
	 * 
	 * @param list of tags
	 * @return the tagged post
	 */
	public VotablePost tagItWith(String name) {
		if (!(name.equals("") || name.isEmpty() || name.equals(null))) {
			tags.add(Tag.findOrCreateByName(name));
		}
		return this;
	}

	/**
	 * used in Application.java for display tagged.html
	 * 
	 * @return all questions witch matches one of the specified tags
	 */
	public static List<VotablePost> findTaggedWith(String... tags) {
		List<VotablePost> hits = new ArrayList<VotablePost>();
		hits = Question
				.find(
						"select distinct p from Question p join p.tags as t where t.name in (:tags)")
				.bind("tags", tags).fetch();
		return hits;
	}

	/**
	 * Gets similar questions. For a specific number of tags.
	 * 
	 * @param minimumtags
	 *            specifies the numbers of equal tags
	 * @param all
	 *            badgeTags form the user which wrote the answer
	 * @param user
	 *            the user which wrote the answer
	 * @return the random number of similar questions
	 */
	public ArrayList<VotablePost> getNotAnsweredSimilarPosts(int minimumtags,
			Set<Tag> tags, User user) {

		Set<VotablePost> posts = this.getSimilarPosts(minimumtags, tags);
		ArrayList<VotablePost> removeposts = new ArrayList<VotablePost>();
		for (VotablePost post : posts) {
			for (Answer answer : ((Question) post).answers) {
				if (answer.author.equals(user)) {
					removeposts.add(post);
				}
			}
		}
		posts.removeAll(removeposts);
		ArrayList<Integer> randoms = this.getRandom(posts.size(),
				this.RAND_NUMBER, posts);

		ArrayList<VotablePost> oldposts = new ArrayList<VotablePost>(posts);
		ArrayList<VotablePost> newposts = new ArrayList<VotablePost>();

		for (int i : randoms) {
			newposts.add(oldposts.get(i));
		}

		return newposts;
	}

	/**
	 * Gets the several random numbers in a array.
	 * 
	 * @param randscope
	 *            the area of the random number
	 * @param number
	 *            how many numbers you wish
	 * @param posts
	 *            the posts
	 * @return array of randoms
	 */
	private ArrayList<Integer> getRandom(int randscope, int number,
			Set<VotablePost> posts) {

		Set<Integer> randnumb = new HashSet();
		Random rand = new Random();

		if (randscope < number) {
			number = randscope;
		}

		while (randnumb.size() < number) {
			randnumb.add(rand.nextInt(randscope));
		}

		return new ArrayList<Integer>(randnumb);
	}

	/**
	 * @param specifies
	 *            the numbers of equal tags (minimum)
	 * @return all questions or answers with enough equal tags
	 */

	public Set<VotablePost> getSimilarPosts(int minimumtags, Set<Tag> tags) {
		Set<VotablePost> set = new HashSet<VotablePost>();
		for (Tag tag : tags) {
			set.addAll(Question.findTaggedWith(tag.name));
		}
		List<VotablePost> posts = new ArrayList<VotablePost>();
		for (VotablePost post : set) {

			int counter = 0;
			for (Tag tag : tags) {

				if (post.tags.contains(tag)) {
					counter++;
				}
			}
			if (counter < minimumtags) {
				posts.add(post);
			}
		}
		set.removeAll(posts);
		set.remove(this);
		return set;
	}

	/**
	 * Vote a Post up or Down
	 * 
	 * @param user the user which has voted
	 * @param result down is false up is true
	 * @return the voted Post
	 */
	public VotablePost vote(User user, boolean result) {
		Vote vote = new Vote(user, this, result).save();
		this.votes.add(vote);

		if (result) {
			this.author.rating.voteUP(this, user);
			this.author.rating.save();
			this.author.save();
		}

		else {

			this.author.rating.voteDown(this, user);
			this.author.rating.save();
			this.author.save();
			user.rating.penalty();
			user.rating.save();
			user.save();
		}
		this.voting();
		this.save();
		return this;
	}

}
package application;

import java.util.List;

//Class to allow for dynamic rows within a table
public class QATableRow {
		public enum RowType {
			QUESTION, ANSWER, REVIEW
		}

		private final RowType type;
		private final String text;
		private final Integer contentId;
		private final Integer authorId;
		private final List<String> relatedId;

		// Wrapper class for resultsTable to allow for dynamic rows
		public QATableRow(RowType type, String text, Integer contentId, List<String> relatedId) {
			this.type = type;
			this.text = text;
			this.contentId = contentId;
			this.authorId = null;
			this.relatedId = relatedId;

		}

		public QATableRow(RowType type, String text, Integer contentId, Integer authorId, List<String> relatedId) {
			this.type = type;
			this.text = text;
			this.contentId = contentId;
			this.authorId = authorId;
			this.relatedId = relatedId;

		}

		public QATableRow(RowType type, String text, Integer contentId, Integer authorId) {
			this.type = type;
			this.text = text;
			this.contentId = contentId;
			this.authorId = authorId;
			this.relatedId = null;

		}

		public Integer getQuestionId() {
			return contentId;
		}

		public Integer getAnswerId() {
			return contentId;
		}

		public Integer getReviewId() {
			return contentId;
		}

		public Integer getAuthorId() {
			return authorId;
		}
		
		public Integer getContentId() {
			return contentId;
		}

		public String getText() {
			return text;
		}

		public RowType getType() {
			return type;
		}

		public List<String> getRelatedId() {
			return relatedId;
		}

		@Override
		public String toString() {
			return text;
		}
	}
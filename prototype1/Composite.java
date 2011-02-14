

	public class Composite implements Comparable<Composite> {
		DataUserNode user;
		DataUPage page;
		double crossProbability;
		
		public Composite(DataUserNode user, DataUPage page , double proba) {
			this.user=user;
			this.page =page;
			this.crossProbability = proba; //la proba P(A inter B)
		}

		@Override
		public boolean equals(Object obj) {
			try {
				Composite c = (Composite) obj;
				return ( this.page.getMongoId()==c.page.getMongoId() && this.user.getMongoId()==c.user.getMongoId());
			
			}catch(Exception e){ return false;}
			
		}

		
		public int compareTo(Composite arg0) {
			if (this == arg0) return 0;
			if (this.crossProbability < arg0.crossProbability) return -1;
			if (this.crossProbability > arg0.crossProbability) return 1;
			//same proba;
			if (this.page==null && arg0.page==null) return 0;
			if (this.page==null) return -1;
			if (arg0.page==null) return 1;
			return this.page.getUrl().compareTo(arg0.page.getUrl());
		}

		@Override
		public String toString() {
			if (user==null)
				return "Composite [Proba=" + crossProbability
				+ ", page=" + page + ", user=" + user+ "]";
			return "Composite [Proba=" + crossProbability
					+ ", page=" + page + ", user=" + user.getId() + "]";
		}

	}
	

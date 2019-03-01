package com.dangdang.reader.dread.format.txt;

import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Chapter;

public class TxtBook extends Book {

	@Override
	public BaseNavPoint getNavPoint(Chapter chapter) {

		if(chapter == null){
			return null;
		}
		final TxtChapter txtChapter = (TxtChapter) chapter;

		TxtNavPoint navPoint = new TxtNavPoint();
		navPoint.setPath(txtChapter.getPath());
		navPoint.setName(txtChapter.getChapterName());
		navPoint.setStartByte(txtChapter.getStartByte());
		navPoint.setEndByte(txtChapter.getEndByte());

		return navPoint;
	}

	public static class TxtNavPoint extends BaseNavPoint {

		private String path;
		private int startByte;
		private int endByte;
		private int splitChapterNum = 0;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public int getStartByte() {
			return startByte;
		}

		public void setStartByte(int startByte) {
			this.startByte = startByte;
		}

		public int getEndByte() {
			return endByte;
		}

		public void setEndByte(int endByte) {
			this.endByte = endByte;
		}

		public String getName() {
			return lableText;
		}

		public void setName(String name) {
			this.lableText = name;
		}

		public int getSplitChapterNum() {
			return splitChapterNum;
		}

		public void setSplitChapterNum(int splitChapterNum) {
			this.splitChapterNum = splitChapterNum;
		}

		public String getTagPath() {
			final StringBuffer pathSb = new StringBuffer(path);
			pathSb.append(":");
			pathSb.append(startByte);
			pathSb.append("-");
			pathSb.append(endByte);

			return pathSb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + endByte;
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			result = prime * result + splitChapterNum;
			result = prime * result + startByte;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TxtNavPoint other = (TxtNavPoint) obj;
			if (endByte != other.endByte)
				return false;
			if (path == null) {
				if (other.path != null) {
					return false;
				}
			} else if (!path.equals(other.path))
				return false;
			if (splitChapterNum != other.splitChapterNum)
				return false;
			if (startByte != other.startByte)
				return false;
			return true;
		}

	}

}

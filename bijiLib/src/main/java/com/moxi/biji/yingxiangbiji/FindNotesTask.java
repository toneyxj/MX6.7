package com.moxi.biji.yingxiangbiji;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteSearchHelper;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;

import java.util.List;

/**
 * @author rwondratschek
 */
public class FindNotesTask {

    private final EvernoteSearchHelper.Search mSearch;

    public FindNotesTask(@Nullable Notebook notebook, @Nullable String title) {
        NoteFilter noteFilter = new NoteFilter();
        noteFilter.setOrder(NoteSortOrder.UPDATED.getValue());

        if (!TextUtils.isEmpty(title)) {
            noteFilter.setWords(title);
        }
        if (notebook != null) {
            noteFilter.setNotebookGuid(notebook.getGuid());
        }
        mSearch = new EvernoteSearchHelper.Search()
                .setOffset(0)
                .setMaxNotes(200)
                .setNoteFilter(noteFilter);
        mSearch.addScope(EvernoteSearchHelper.Scope.PERSONAL_NOTES);
    }

    public List<NoteRef> checkedExecute() throws Exception {
        EvernoteSearchHelper.Result searchResult = EvernoteSession.getInstance()
                .getEvernoteClientFactory()
                .getEvernoteSearchHelper()
                .execute(mSearch);

        Log.e("list", searchResult.getAllAsNoteRef().toString());

        return searchResult.getAllAsNoteRef();
    }
}

package dynamic.type.inferences.handlers;

import com.intellij.codeInsight.editorActions.CopyPastePostProcessor;
import com.intellij.codeInsight.editorActions.FoldingData;
import com.intellij.codeInsight.editorActions.FoldingTransferableData;
import com.intellij.codeInsight.folding.CodeFoldingManager;
import com.intellij.codeInsight.folding.impl.CodeFoldingManagerImpl;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CopyPasteHandler extends CopyPastePostProcessor<FoldingTransferableData> {

    @NotNull
    @Override
    public List<FoldingTransferableData> collectTransferableData(final PsiFile file,
                                                                 final Editor editor,
                                                                 final int[] startOffsets,
                                                                 final int[] endOffsets) {

        final ArrayList<FoldingData> list = new ArrayList<FoldingData>();
        final FoldRegion[] regions = editor.getFoldingModel().getAllFoldRegions();
        for (final FoldRegion region : regions) {
            if (!region.isValid()) continue;
            for (int j = 0; j < startOffsets.length; j++) {
                if (startOffsets[j] <= region.getStartOffset() && region.getEndOffset() <= endOffsets[j]) {
                    list.add(
                            new FoldingData(
                                    region.getStartOffset() - startOffsets[j],
                                    region.getEndOffset() - startOffsets[j],
                                    region.isExpanded()
                            )
                    );
                }
            }
        }

        return Collections.singletonList(new FoldingTransferableData(list.toArray(new FoldingData[0])));
    }

    @NotNull
    @Override
    public List<FoldingTransferableData> extractTransferableData(final Transferable content) {
        FoldingTransferableData foldingData = null;
        try {
            final DataFlavor flavor = FoldingData.getDataFlavor();
            if (flavor != null) {
                foldingData = (FoldingTransferableData) content.getTransferData(flavor);
            }
        } catch (UnsupportedFlavorException | IOException ignored) {
        }
        if (foldingData != null) {
            return Collections.singletonList(foldingData);
        }

        return Collections.emptyList();
    }

    @Override
    public void processTransferableData(final Project project,
                                        final Editor editor,
                                        final RangeMarker bounds,
                                        int caretOffset,
                                        Ref<Boolean> indented,
                                        final List<FoldingTransferableData> values) {
        assert values.size() == 1;
        final FoldingTransferableData value = values.get(0);
        if (value.getData().length == 0) return;

        final CodeFoldingManagerImpl foldingManager = (CodeFoldingManagerImpl) CodeFoldingManager.getInstance(project);
        foldingManager.updateFoldRegions(editor, true);

        Runnable operation = new Runnable() {
            @Override
            public void run() {
                for (FoldingData data : value.getData()) {
                    FoldRegion region = foldingManager.findFoldRegion(editor, data.startOffset + bounds.getStartOffset(), data.endOffset + bounds.getStartOffset());
                    if (region != null) {
                        region.setExpanded(data.isExpanded);
                    }
                }
            }
        };
        editor.getFoldingModel().runBatchFoldingOperation(operation);
    }
}

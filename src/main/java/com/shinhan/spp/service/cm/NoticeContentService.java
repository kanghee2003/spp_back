package com.shinhan.spp.service.cm;

import com.shinhan.spp.dto.cm.NoticeContentPart;
import com.shinhan.spp.exception.custom.BusinessException;
import com.shinhan.spp.util.Utf8ChunkUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class NoticeContentService {

    private static final int PART_MAX_BYTES = 2000;

    private static final Pattern BASE64_IMG_PATTERN =
            Pattern.compile("<img[^>]*src=[\"']data:image/[^\"']*[\"'][^>]*>", Pattern.CASE_INSENSITIVE);


    @Transactional
    public void saveHtmlParts(Long noticeId, String html) {
        // 1) base64 이미지 차단
        this.validateNoBase64Image(html);

        // 2) UTF-8 2000B 분할
        List<String> chunks = Utf8ChunkUtil.splitByUtf8Bytes(html, PART_MAX_BYTES);

        // 3) seq 부여
        List<NoticeContentPart> parts = new ArrayList<>(chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            String part = chunks.get(i);

            int b = Utf8ChunkUtil.utf8Bytes(part);
            if (b > PART_MAX_BYTES) {
                throw new BusinessException("part 사이즈가 2000Byte를 초과합니다.: " + b);
            }

            NoticeContentPart row = new NoticeContentPart();
            row.setNoticeId(noticeId);
            row.setSeq(i + 1);
            row.setContentPart(part);
            parts.add(row);
        }
    }


    private void validateNoBase64Image(String html) {
        if (html == null) return;
        if (BASE64_IMG_PATTERN.matcher(html).find()) {
            throw new BusinessException("Base64 이미지(data:image)는 허용되지 않습니다. 이미지는 업로드 후 삽입해주세요.");
        }
    }
}
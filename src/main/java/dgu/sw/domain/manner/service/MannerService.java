package dgu.sw.domain.manner.service;

import dgu.sw.domain.manner.dto.MannerDetailDTO;
import dgu.sw.domain.manner.dto.MannerListDTO;
import dgu.sw.domain.manner.entity.Manner;
import dgu.sw.domain.manner.repository.MannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MannerService {
    private final MannerRepository mannerRepository;

    public List<String> getCategories() {
        return mannerRepository.findAll()
                .stream()
                .map(Manner::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<MannerListDTO> getMannersByCategory(String category) {
        return mannerRepository.findByCategory(category)
                .stream()
                .map(manner -> new MannerListDTO(
                        manner.getCategory(),
                        manner.getTitle(),
                        manner.getContent().length() > 20 ? manner.getContent().substring(0, 20) + "..." : manner.getContent(),
                        manner.getImageUrl()
                ))
                .collect(Collectors.toList());
    }

    public MannerDetailDTO getMannerDetail(Long mannerId) {
        Manner manner = mannerRepository.findById(mannerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid mannerId: " + mannerId));

        return new MannerDetailDTO(
                manner.getTitle(),
                manner.getContent(),
                manner.getImageUrl()
        );
    }
}

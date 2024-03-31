package org.enrico.totemsoftware.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Triple;

import lombok.NonNull;

public class OnlyComparator<T, K> {

	private List<T> list;
	private Map<K, List<T>> mappedList;

	public OnlyComparator(@NonNull List<T> list, @NonNull Function<T, K> discriminant) {
		this.list = list;
		this.mappedList = list.stream().collect(Collectors.groupingBy(discriminant));
	}

	public boolean isOnlyOne() {
		return list.isEmpty() || mappedList.keySet().size() == 1;
	}

	public List<T> getListByValue(K value) {
		return Optional.ofNullable(mappedList.get(value)).orElseThrow();
	}

	public List<T> getOriginalList() {
		return list.stream().toList();
	}

	public K getOnlyValue() {
		if (isOnlyOne()) {
			return mappedList.keySet().stream().toList().getFirst();
		} else {
			throw new IllegalStateException();
		}
	}

	public Stream<Triple<T, T, T>> onlyAdiacentStream() {
		List<Triple<T, T, T>> result = new ArrayList<>();

		ListIterator<T> iterator = list.listIterator();
		while (iterator.hasNext()) {
			T prev = null, next = null;
			T curr = iterator.next();
			if (iterator.hasPrevious()) {
				prev = list.get(iterator.previousIndex());
			}
			if (iterator.hasNext()) {
				next = list.get(iterator.nextIndex());
			}
			result.add(Triple.of(prev, curr, next));
		}

		return result.stream();
	}
}

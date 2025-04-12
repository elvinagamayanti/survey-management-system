/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sms.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.sms.dto.ProgramDto;
import com.example.sms.entity.Program;
import com.example.sms.mapper.ProgramMapper;
import com.example.sms.repository.ProgramRepository;
import com.example.sms.service.ProgramService;

/**
 *
 * @author pinaa
 */
@Service
public class ProgramServiceImpl implements ProgramService {
    private ProgramRepository programRepository;
    
    public ProgramServiceImpl(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }
    
    @Override
    public List<ProgramDto> ambilDaftarProgram() {
        List<Program> programs = this.programRepository.findAll();
        List<ProgramDto> programDtos = programs.stream()
                .map((program) -> (ProgramMapper.mapToProgramDto(program)))
                .collect(Collectors.toList());        
        return programDtos;
    }
    
    @Override
    public void hapusDataProgram(Long programId) {
        programRepository.deleteById(programId);        
    }
    
    @Override
    public void perbaruiDataProgram(ProgramDto programDto) {
        Program program = ProgramMapper.mapToProgram(programDto);
        System.out.println(programDto);
        programRepository.save(program);
    }
    
    @Override
    public void simpanDataProgram(ProgramDto programDto) {
        Program program = ProgramMapper.mapToProgram(programDto);
        programRepository.save(program);
    }

    @Override
    public ProgramDto cariProgramById(Long id) {
        Program program = programRepository.findById(id).get();
        return ProgramMapper.mapToProgramDto(program);
    }
}
